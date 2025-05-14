package com.ntz.distributor_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ntz.distributor_app.data.model.AgentData
import com.ntz.distributor_app.data.repository.AgentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface AgentUiState {
    object Loading: AgentUiState
    data class Success(val agents: List<AgentData>): AgentUiState
    data class Error(val message: String): AgentUiState
}

@HiltViewModel
class AgentViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AgentUiState>(AgentUiState.Loading)
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    




}