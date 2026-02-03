package com.cebolao.domain.repository

import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

interface UserPresetRepository {
    suspend fun savePreset(preset: UserFilterPreset): AppResult<Unit>

    suspend fun deletePreset(name: String): AppResult<Unit>

    suspend fun getAllPresets(): AppResult<List<UserFilterPreset>>

    fun observePresets(): Flow<List<UserFilterPreset>>
}
