package com.cebolao.domain.usecase

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckGameUseCaseTest {
    @Test
    fun `dupla sena modes produce expected hits`() {
        val profile =
            LotteryProfile(
                type = LotteryType.DUPLA_SENA,
                name = "Dupla Sena",
                minNumber = 1,
                maxNumber = 50,
                numbersPerGame = 6,
                prizeRanges = listOf(6, 5, 4, 3),
                costPerGame = 250,
            )

        val contest =
            Contest(
                id = 100,
                lotteryType = LotteryType.DUPLA_SENA,
                drawDate = "01/01/2026",
                numbers = listOf(1, 2, 3, 4, 5, 6),
                secondDrawNumbers = listOf(6, 7, 8, 9, 10, 11),
                teamNumber = null,
            )

        val game =
            Game(
                id = "g1",
                lotteryType = LotteryType.DUPLA_SENA,
                numbers = listOf(1, 7, 8, 12, 13, 14),
                createdAt = 1,
            )

        val useCase = CheckGameUseCase()

        val resFirst = useCase(game, contest, profile, DuplaMode.FIRST)
        val resSecond = useCase(game, contest, profile, DuplaMode.SECOND)
        val resBest = useCase(game, contest, profile, DuplaMode.BEST)

        // FIRST: only 1 matches (1)
        assertEquals(1, resFirst.hits)
        // SECOND: matches 6 and 7 => 2 hits
        assertEquals(2, resSecond.hits)
        // BEST: max(1,2) => 2
        assertEquals(2, resBest.hits)
    }

    @Test
    fun `lotomania 0 hits is a prize`() {
        val profile =
            LotteryProfile(
                type = LotteryType.LOTOMANIA,
                name = "Lotomania",
                minNumber = 0,
                maxNumber = 99,
                numbersPerGame = 50,
                prizeRanges = listOf(20, 19, 18, 17, 16, 0),
                costPerGame = 300,
            )

        val contestNumbers = (0..19).toList() // 0 to 19 (20 numbers)
        val gameNumbers = (20..69).toList() // 20 to 69 (50 numbers) - NO INTERSECTION with contest

        val contest =
            Contest(
                id = 1,
                lotteryType = LotteryType.LOTOMANIA,
                drawDate = "01/01/2026",
                numbers = contestNumbers,
            )
        val game =
            Game(
                id = "g1",
                lotteryType = LotteryType.LOTOMANIA,
                numbers = gameNumbers,
                createdAt = 1,
            )

        val useCase = CheckGameUseCase()
        val result = useCase(game, contest, profile)

        assertEquals(0, result.hits)
        assertEquals(true, result.isPrize)
    }
}
