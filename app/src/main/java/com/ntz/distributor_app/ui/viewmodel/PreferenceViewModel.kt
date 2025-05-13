package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntz.distributor_app.data.model.User
import com.ntz.distributor_app.data.model.UserPreferences
import com.ntz.distributor_app.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PreferenceUiState {
    object Loading: PreferenceUiState
    data class Success(val currentPreferences: User, val isSaving: Boolean = true): PreferenceUiState
    data class Error(val message: String): PreferenceUiState
}

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<PreferenceUiState>(PreferenceUiState.Loading)
    val uiState: StateFlow<PreferenceUiState> = _uiState.asStateFlow()

    internal val _editablePreferences = MutableStateFlow<User?>(null)
    val editablePrefences: StateFlow<User?> = _editablePreferences.asStateFlow()

    init {
        loadPrefences()
    }

    private fun loadPrefences() {
        viewModelScope.launch {
            _uiState.value = PreferenceUiState.Loading
            preferenceRepository.getUserPreferences()
                .catch { e ->
                    Log.e("PreferenceViewModel", "Error loading preferences: ${e.message}")
                    _uiState.value = PreferenceUiState.Error("Error loading preferences: ${e.message}")
                }
                .collect { prefs ->
                    Log.d("PreferenceViewModel", "Loaded preferences: $prefs")
                    if (_uiState.value !is PreferenceUiState.Success || !(_uiState.value as PreferenceUiState.Success).isSaving) {
                        _uiState.value = PreferenceUiState.Success(prefs)
                        if (_editablePreferences.value == null) {
                            _editablePreferences.value = prefs
                        }
                    }
                }
        }
    }

    /*fun updateEditableCategory(category: String, isChecked: Boolean) {
        _editablePreferences.update { currentPrefs ->
            currentPrefs?.copy(preferredCategories = currentPrefs.preferredCategories.toMutableSet().apply {
                if (isChecked) add(category) else remove(category)
            })
        }
    }

    fun updateEditableLocation(location: String) {
        _editablePreferences.update { it?.copy(preferredLocation = location) }
    }
    fun updateEditableMinOrder(minOrderString: String) {
        _editablePreferences.update { it?.copy(minOrder = minOrderString.toIntOrNull()) }
    }*/

    fun updateEditableUser(user: User) {
        _editablePreferences.update { user }
    }

    fun updateEditableUserType(userType: String) {
        _editablePreferences.update { it?.copy(userType = userType) }
    }

    fun savePreferences() {
        val prefsToSave = _editablePreferences.value
        if (prefsToSave != null) {
            (_uiState.value as? PreferenceUiState.Success)?.let { currentState ->
                _uiState.value = currentState.copy(isSaving = true)
            } ?: run {
                Log.w("PreferenceViewModel", "Current state is not Success, cannot save preferences.")
                return
            }

            viewModelScope.launch {
                try {
                    preferenceRepository.saveUser(prefsToSave)
                    Log.d("PreferenceViewModel", "Preferences saved successfully: $prefsToSave")
                    _uiState.value = PreferenceUiState.Success(prefsToSave, isSaving = false)
                } catch (e: Exception) {
                    Log.d("PreferenceViewModel", "Error saving preferences: ${e.message}")
                    _uiState.value = PreferenceUiState.Error("Error saving preferences: ${e.message}")
                }
            }
        } else {
            Log.w("PreferenceViewModel", "No preferences to save.")
        }
    }
}