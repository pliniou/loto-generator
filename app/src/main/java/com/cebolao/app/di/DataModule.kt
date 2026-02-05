package com.cebolao.app.di

import android.content.Context
import androidx.room.Room
import com.cebolao.data.local.UserPresetDataStoreRepository
import com.cebolao.data.local.room.LotteryDatabase
import com.cebolao.data.local.room.dao.LotteryDao
import com.cebolao.data.repository.LotteryRepositoryImpl
import com.cebolao.data.repository.ProfileRepositoryImpl
import com.cebolao.data.repository.SettingsRepositoryImpl
import com.cebolao.data.repository.UserStatisticsRepositoryImpl
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.SettingsRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.repository.UserStatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindLotteryRepository(impl: LotteryRepositoryImpl): LotteryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindUserPresetRepository(impl: UserPresetDataStoreRepository): UserPresetRepository

    @Binds
    @Singleton
    abstract fun bindUserStatisticsRepository(impl: UserStatisticsRepositoryImpl): UserStatisticsRepository

    companion object {
        @Provides
        @Singleton
        fun provideLotteryDatabase(
            @ApplicationContext context: Context,
        ): LotteryDatabase {
            return Room.databaseBuilder(
                context,
                LotteryDatabase::class.java,
                "lottery_database",
            ).build()
        }

        @Provides
        fun provideLotteryDao(database: LotteryDatabase): LotteryDao {
            return database.lotteryDao()
        }
    }
}
