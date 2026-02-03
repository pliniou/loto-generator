package com.cebolao.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolao.data.remote.dto.PrizeDto
import com.cebolao.domain.model.LotteryType

@Entity(tableName = "contests")
data class ContestEntity(
    @PrimaryKey val id: String, // Format: "LOTTERYTYPE_CONTESTID"
    val lotteryType: LotteryType,
    val contestNumber: Int,
    val date: String,
    val numbers: List<String>,
    val secondDrawNumbers: List<String>? = null,
    val teamName: String? = null,
    val nextContest: Int? = null,
    val nextContestDate: String? = null,
    val nextContestEstimatedPrize: Double? = null,
    val accumulated: Boolean? = null,
    val prizeList: List<PrizeDto>? = null,
)
