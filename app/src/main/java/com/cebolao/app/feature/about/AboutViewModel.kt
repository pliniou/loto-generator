package com.cebolao.app.feature.about

import androidx.lifecycle.ViewModel
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AboutUiState(
    val profiles: List<LotteryProfile> = emptyList(),
)

@HiltViewModel
class AboutViewModel
    @Inject
    constructor(
        profileRepository: ProfileRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AboutUiState())
        val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

        init {
            val profiles = profileRepository.getAllProfiles()
            _uiState.value = AboutUiState(profiles)
        }
    }
