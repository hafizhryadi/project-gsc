package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntz.distributor_app.data.model.Distributor
import com.ntz.distributor_app.data.repository.DistributorRepository
import com.ntz.distributor_app.data.repository.PreferenceRepository
import com.ntz.distributor_app.data.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AllDistributorsUiState {
    object Loading : AllDistributorsUiState
    data class Success(val distributors: List<Distributor>) : AllDistributorsUiState
    data class Error(val message: String, val cause: Throwable? = null) : AllDistributorsUiState
}

sealed interface RecommendationUiState {
    object Idle : RecommendationUiState
    object Loading : RecommendationUiState
    data class Success(val distributors: List<Distributor>) : RecommendationUiState
    data class Error(val message: String, val cause: Throwable? = null) : RecommendationUiState
}

@HiltViewModel
class DistributorViewModel @Inject constructor(
    private val distributorRepository: DistributorRepository,
    private val preferenceRepository: PreferenceRepository,
    private val recommendationRepository: RecommendationRepository
): ViewModel() {
    internal val _allDistributorsState = MutableStateFlow<AllDistributorsUiState>(AllDistributorsUiState.Loading)
    val allDistributorState: StateFlow<AllDistributorsUiState> = _allDistributorsState.asStateFlow()

    private val _recommendationState = MutableStateFlow<RecommendationUiState>(RecommendationUiState.Idle)
    val recommendationState: StateFlow<RecommendationUiState> = _recommendationState.asStateFlow()

    private var cachedDistributors: List<Distributor> = emptyList()

    init {
        loadAllDistributors()
    }

    fun loadAllDistributors(forceRefresh: Boolean = false) {
        if (!forceRefresh && _allDistributorsState.value is AllDistributorsUiState.Success) {
            Log.d("DistributorViewModel", "All distributors already loaded.")
            return
        }

        viewModelScope.launch {
            _allDistributorsState.value = AllDistributorsUiState.Loading
            try {
                val distributors = distributorRepository.getAllDistributors()
                cachedDistributors = distributors
                _allDistributorsState.value = AllDistributorsUiState.Success(distributors)
                Log.i("DistributorViewModel", "Loaded distributors: $distributors")
            } catch (e: Exception) {
                Log.e("DistributorViewModel", "Error loading distributors: ${e.message}", e)
                _allDistributorsState.value = AllDistributorsUiState.Error("Error loading distributors: ${e.message}", e)
            }
        }
    }

    fun getRecommendations() {
        val currentDistributors = cachedDistributors
        if (currentDistributors.isEmpty()) {
            Log.w("DistributorViewModel", "No distributors available for recommendations.")
            _recommendationState.value = RecommendationUiState.Error("No distributors available for recommendations.")
            loadAllDistributors()
            return
        }

        viewModelScope.launch {
            _recommendationState.value = RecommendationUiState.Loading
            try {
                val userPreferences = preferenceRepository.getUserPreferences().firstOrNull()
                    ?: run {
                        Log.w("DistributorViewModel", "User preferences not found.")
                        _recommendationState.value = RecommendationUiState.Error("User preferences not found.")
                        return@launch
                    }

                Log.d("DistributorViewModel", "User preferences: $userPreferences")
                val recommendationResult = recommendationRepository.getRecommendations(preferences = userPreferences, availableDistributor =  currentDistributors)

                recommendationResult.onSuccess { recommendedNames ->
                    Log.d("DistributorViewModel", "Recommended names: $recommendedNames")
                    val recommendedDistributorObjects = currentDistributors.filter { distributor ->
                        recommendedNames.any {recName -> recName.equals(distributor.name, ignoreCase = true)}
                    }
                    Log.i("DistributorViewModel", "Recommended distributors: $recommendedDistributorObjects")
                    _recommendationState.value = RecommendationUiState.Success(recommendedDistributorObjects)
                }.onFailure { exception ->
                    Log.e("DistributorViewModel", "Error generating recommendations: ${exception.message}", exception)
                    _recommendationState.value = RecommendationUiState.Error(message = "Error generating recommendations: ${exception.message}", cause =  exception)
                }
            } catch (e: Exception) {
                Log.e("DistributorViewModel", "Error generating recommendations: ${e.message}", e)
                _recommendationState.value = RecommendationUiState.Error(message = "Error generating recommendations: ${e.message}", cause =  e)
            }
        }
    }

    fun refreshDistributors() {
        _recommendationState.value = RecommendationUiState.Idle
        loadAllDistributors(forceRefresh = true)
    }
}