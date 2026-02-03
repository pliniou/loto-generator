package com.cebolao.domain.repository

import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType

/**
 * Repositório read-only de profiles de loterias.
 */
interface ProfileRepository {
    fun getProfile(type: LotteryType): LotteryProfile

    fun getAllProfiles(): List<LotteryProfile>
}
