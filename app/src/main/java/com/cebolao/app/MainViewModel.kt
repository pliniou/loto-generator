package com.cebolao.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.navigation.Route
import com.cebolao.domain.repository.SettingsRepository
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        private val _isLoading = MutableStateFlow(true)
        val isLoading = _isLoading.asStateFlow()

        private val _startDestination = MutableStateFlow<Route>(Route.Home)
        val startDestination = _startDestination.asStateFlow()

        init {
            viewModelScope.launch {
                // Delay para garantir que a splash screen seja visível
                when (val result = settingsRepository.isOnboardingCompleted()) {
                    is AppResult.Success -> {
                        _startDestination.value =
                            if (!result.value) {
                                Route.Onboarding
                            } else {
                                Route.Home
                            }
                    }
                    is AppResult.Failure -> {
                        // Em caso de erro, assume onboarding não concluído para evitar bloquear usuário.
                        _startDestination.value = Route.Onboarding
                    }
                }
                _isLoading.value = false
            }
        }
    }
