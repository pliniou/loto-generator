package com.cebolao.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.domain.repository.SettingsRepository
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardUiState(
    val isLoading: Boolean = false,
)

@HiltViewModel
class OnboardViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(OnboardUiState())
        val uiState: StateFlow<OnboardUiState> = _uiState.asStateFlow()

        fun completeOnboarding(onCompleted: () -> Unit) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                when (settingsRepository.setOnboardingCompleted()) {
                    is AppResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onCompleted()
                    }
                    is AppResult.Failure -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }
