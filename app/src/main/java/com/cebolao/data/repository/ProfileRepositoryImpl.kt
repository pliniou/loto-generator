package com.cebolao.data.repository

import com.cebolao.data.local.AssetsReader
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de profiles usando AssetsReader.
 * Profiles são cached em memória (imutáveis).
 */
@Singleton
class ProfileRepositoryImpl
    @Inject
    constructor(
        private val assetsReader: AssetsReader,
    ) : ProfileRepository {
        // Cache em memória (inicialização preguiçosa)
        private val profilesCache: Map<LotteryType, LotteryProfile> by lazy {
            assetsReader.readAllProfiles().associateBy { it.type }
        }

        override fun getProfile(type: LotteryType): LotteryProfile =
            profilesCache[type] ?: throw IllegalArgumentException("Unknown lottery type: $type")

        override fun getAllProfiles(): List<LotteryProfile> = profilesCache.values.toList()
    }
