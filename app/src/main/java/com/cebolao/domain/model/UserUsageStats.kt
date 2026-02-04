package com.cebolao.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserUsageStats(
    val presetName: String,
    val usageCount: Int = 0,
    val savedGamesCount: Int = 0,
    val totalHits: Int = 0,
    val lastUsed: Long = 0L,
)
