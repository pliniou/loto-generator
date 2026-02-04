package com.cebolao.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolao.domain.model.LotteryType

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val lotteryType: LotteryType,
    val numbers: List<String>,
    val createdAt: Long, // Timestamp
    val isPinned: Boolean = false,
    val secondDrawNumbers: List<String>? = null, // For Dupla Sena
    val teamName: String? = null, // For Timemania
    val recentHitRate: Float = 0f,
    val historicalHitRate: Float = 0f,
    val sourcePreset: String? = null,
)
