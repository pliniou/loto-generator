package com.cebolao.data.remote.mapper

import com.cebolao.data.local.room.entity.GameEntity
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.TimemaniaUtil

object GameMapper {
    fun toDomain(entity: GameEntity): Game {
        val rawNumbers = entity.numbers.map { it.toInt() }
        val numbers =
            if (entity.lotteryType == LotteryType.SUPER_SETE) {
                rawNumbers
            } else {
                rawNumbers.sorted()
            }
        val rawSecondDraw = entity.secondDrawNumbers?.map { it.toInt() }
        val secondDraw =
            if (entity.lotteryType == LotteryType.SUPER_SETE) {
                rawSecondDraw
            } else {
                rawSecondDraw?.sorted()
            }
        val teamNumber = entity.teamName?.let { TimemaniaUtil.getTeamId(it) }

        return Game(
            id = entity.id,
            lotteryType = entity.lotteryType,
            numbers = numbers,
            secondDrawNumbers = secondDraw,
            teamNumber = teamNumber,
            isPinned = entity.isPinned,
            createdAt = entity.createdAt,
            recentHitRate = entity.recentHitRate,
            historicalHitRate = entity.historicalHitRate,
            sourcePreset = entity.sourcePreset,
        )
    }

    fun toEntity(domain: Game): GameEntity {
        val teamName = domain.teamNumber?.let { TimemaniaUtil.getTeamName(it) }

        return GameEntity(
            id = domain.id,
            lotteryType = domain.lotteryType,
            numbers = domain.numbers.map { it.toString() },
            createdAt = domain.createdAt,
            isPinned = domain.isPinned,
            secondDrawNumbers = domain.secondDrawNumbers?.map { it.toString() },
            teamName = teamName,
            recentHitRate = domain.recentHitRate,
            historicalHitRate = domain.historicalHitRate,
            sourcePreset = domain.sourcePreset,
        )
    }
}
