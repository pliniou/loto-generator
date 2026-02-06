package com.cebolao.data.remote.mapper

import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.remote.dto.ContestDto
import com.cebolao.data.remote.dto.PrizeDto
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.Prize

/**
 * Mapper para converter DTOs de rede para models de domínio e Room.
 */
object ContestMapper {
    // --- DTO -> Entity ---
    fun toEntity(
        dto: ContestDto,
        type: LotteryType,
    ): ContestEntity {
        // ID único para o banco: TIPO_NUMERO
        val uniqueId = "${type.name}_${dto.id}"
        return ContestEntity(
            id = uniqueId,
            lotteryType = type,
            contestNumber = dto.id,
            date = dto.date,
            numbers = dto.numbers,
            secondDrawNumbers = dto.secondDrawNumbers,
            teamName = dto.teamName,
            nextContest = dto.nextContest,
            nextContestDate = dto.nextContestDate,
            nextContestEstimatedPrize = dto.nextContestEstimatedPrize,
            accumulated = dto.accumulated,
            prizeList = dto.prizeList,
        )
    }

    // --- Domain -> Entity ---
    fun toEntity(domain: Contest): ContestEntity {
        val uniqueId = "${domain.lotteryType.name}_${domain.id}"
        return ContestEntity(
            id = uniqueId,
            lotteryType = domain.lotteryType,
            contestNumber = domain.id,
            date = domain.drawDate,
            numbers = domain.numbers.map { it.toString() },
            secondDrawNumbers = domain.secondDrawNumbers?.map { it.toString() },
            teamName = domain.teamNumber?.let { com.cebolao.domain.util.TimemaniaUtil.getTeamName(it) },
            nextContest = domain.nextContest,
            nextContestDate = domain.nextContestDate,
            nextContestEstimatedPrize = domain.nextContestEstimatedPrize,
            accumulated = domain.accumulated,
            prizeList = domain.prizeList?.map { PrizeDto(it.range, it.winners, it.prizeValue, it.description) },
        )
    }

    // --- Entity -> Domain ---
    fun toDomain(entity: ContestEntity): Contest {
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
        val teamNumber = entity.teamName?.let { com.cebolao.domain.util.TimemaniaUtil.getTeamId(it) }

        return Contest(
            id = entity.contestNumber,
            lotteryType = entity.lotteryType,
            drawDate = entity.date,
            numbers = numbers,
            secondDrawNumbers = secondDraw,
            teamNumber = teamNumber,
            nextContest = entity.nextContest,
            nextContestDate = entity.nextContestDate,
            nextContestEstimatedPrize = entity.nextContestEstimatedPrize,
            accumulated = entity.accumulated ?: false,
            prizeList = entity.prizeList?.map { Prize(it.range, it.winners, it.prizeValue, it.description) },
        )
    }
}
