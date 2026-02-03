package com.cebolao.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.domain.repository.SettingsRepository
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        fun completeOnboarding(onCompleted: () -> Unit) {
            viewModelScope.launch {
                when (settingsRepository.setOnboardingCompleted()) {
                    is AppResult.Success -> onCompleted()
                    is AppResult.Failure -> Unit
                }
            }
        }
    }
