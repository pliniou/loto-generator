package com.cebolao.domain.repository

import com.cebolao.domain.result.AppResult

interface SettingsRepository {
    suspend fun isOnboardingCompleted(): AppResult<Boolean>

    suspend fun setOnboardingCompleted(): AppResult<Unit>
}
