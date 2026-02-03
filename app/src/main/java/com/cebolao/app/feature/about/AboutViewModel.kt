package com.cebolao.app.feature.about

import androidx.lifecycle.ViewModel
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel
    @Inject
    constructor(
        profileRepository: ProfileRepository,
    ) : ViewModel() {
        // Profiles são estáticos, então podemos expor direto
        val profiles: List<LotteryProfile> = profileRepository.getAllProfiles()
    }
