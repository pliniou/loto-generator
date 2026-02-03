package com.cebolao.data.remote.mapper

import com.cebolao.data.local.room.entity.GameEntity
import com.cebolao.domain.model.Game
import com.cebolao.domain.util.TimemaniaUtil

object GameMapper {
    fun toDomain(entity: GameEntity): Game {
        val numbers = entity.numbers.map { it.toInt() }.sorted()
        val secondDraw = entity.secondDrawNumbers?.map { it.toInt() }?.sorted()
        val teamNumber = entity.teamName?.let { TimemaniaUtil.getTeamId(it) }

        return Game(
            id = entity.id,
            lotteryType = entity.lotteryType,
            numbers = numbers,
            secondDrawNumbers = secondDraw,
            teamNumber = teamNumber,
            isPinned = entity.isPinned,
            createdAt = entity.createdAt,
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
        )
    }
}
