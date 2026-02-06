package com.cebolao.data.remote.mapper

import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.local.room.entity.GameEntity
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperOrderTest {
    @Test
    fun `contest mapper preserves order for super sete`() {
        val entity =
            ContestEntity(
                id = "SUPER_SETE_1",
                lotteryType = LotteryType.SUPER_SETE,
                contestNumber = 1,
                date = "2026-01-01",
                numbers = listOf("7", "1", "9", "0", "3", "5", "2"),
                secondDrawNumbers = null,
                teamName = null,
                nextContest = null,
                nextContestDate = null,
                nextContestEstimatedPrize = null,
                accumulated = null,
                prizeList = null,
            )

        val domain = ContestMapper.toDomain(entity)

        assertEquals(listOf(7, 1, 9, 0, 3, 5, 2), domain.numbers)
    }

    @Test
    fun `contest mapper sorts numbers for non super sete`() {
        val entity =
            ContestEntity(
                id = "MEGA_SENA_1",
                lotteryType = LotteryType.MEGA_SENA,
                contestNumber = 1,
                date = "2026-01-01",
                numbers = listOf("10", "2", "7", "5", "33", "18"),
                secondDrawNumbers = null,
                teamName = null,
                nextContest = null,
                nextContestDate = null,
                nextContestEstimatedPrize = null,
                accumulated = null,
                prizeList = null,
            )

        val domain = ContestMapper.toDomain(entity)

        assertEquals(listOf(2, 5, 7, 10, 18, 33), domain.numbers)
    }

    @Test
    fun `game mapper preserves order for super sete`() {
        val entity =
            GameEntity(
                id = "game_super",
                lotteryType = LotteryType.SUPER_SETE,
                numbers = listOf("7", "1", "9", "0", "3", "5", "2"),
                createdAt = 1L,
            )

        val domain = GameMapper.toDomain(entity)

        assertEquals(listOf(7, 1, 9, 0, 3, 5, 2), domain.numbers)
    }

    @Test
    fun `game mapper sorts numbers for non super sete`() {
        val entity =
            GameEntity(
                id = "game_mega",
                lotteryType = LotteryType.MEGA_SENA,
                numbers = listOf("10", "2", "7", "5", "33", "18"),
                createdAt = 1L,
            )

        val domain = GameMapper.toDomain(entity)

        assertEquals(listOf(2, 5, 7, 10, 18, 33), domain.numbers)
    }
}
