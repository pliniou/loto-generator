package com.cebolao.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.result.appResultSuspend
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_presets")

@Singleton
class UserPresetDataStoreRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : UserPresetRepository {
        private val preferencesKey = stringPreferencesKey("user_presets_json")

        private val json = Json { ignoreUnknownKeys = true }

        override fun observePresets(): Flow<List<UserFilterPreset>> =
            context.dataStore.data.map { prefs ->
                val raw = prefs[preferencesKey] ?: "[]"
                try {
                    json.decodeFromString<List<UserFilterPreset>>(raw)
                } catch (e: Exception) {
                    emptyList()
                }
            }

        override suspend fun savePreset(preset: UserFilterPreset): AppResult<Unit> =
            appResultSuspend {
                context.dataStore.edit { prefs ->
                    val raw = prefs[preferencesKey] ?: "[]"
                    val list =
                        try {
                            json.decodeFromString<MutableList<UserFilterPreset>>(raw)
                        } catch (_: Exception) {
                            mutableListOf()
                        }

                    val idx = list.indexOfFirst { it.name == preset.name }
                    if (idx >= 0) list[idx] = preset else list.add(preset)
                    prefs[preferencesKey] = json.encodeToString(list)
                }
            }

        override suspend fun deletePreset(name: String): AppResult<Unit> =
            appResultSuspend {
                context.dataStore.edit { prefs ->
                    val raw = prefs[preferencesKey] ?: "[]"
                    val list =
                        try {
                            json.decodeFromString<MutableList<UserFilterPreset>>(raw)
                        } catch (_: Exception) {
                            mutableListOf()
                        }
                    val newList = list.filterNot { it.name == name }
                    prefs[preferencesKey] = json.encodeToString(newList)
                }
            }

        override suspend fun getAllPresets(): AppResult<List<UserFilterPreset>> =
            appResultSuspend {
                val prefs = context.dataStore.data.first()
                val raw = prefs[preferencesKey] ?: "[]"
                try {
                    json.decodeFromString(raw)
                } catch (_: Exception) {
                    emptyList()
                }
            }
    }
