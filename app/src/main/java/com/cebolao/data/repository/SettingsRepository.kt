package com.cebolao.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.cebolao.app.di.IoDispatcher
import com.cebolao.domain.repository.SettingsRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.result.appResultSuspend
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : SettingsRepository {
        private val prefs: SharedPreferences by lazy {
            context.getSharedPreferences("cebolao_prefs", Context.MODE_PRIVATE)
        }

        override suspend fun isOnboardingCompleted(): AppResult<Boolean> =
            appResultSuspend {
                withContext(ioDispatcher) {
                    prefs.getBoolean("onboarding_completed", false)
                }
            }

        override suspend fun setOnboardingCompleted(): AppResult<Unit> =
            appResultSuspend {
                withContext(ioDispatcher) {
                    prefs.edit { putBoolean("onboarding_completed", true) }
                }
            }
    }

