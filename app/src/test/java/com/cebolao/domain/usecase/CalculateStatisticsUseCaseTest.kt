package com.cebolao.domain.usecase

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateStatisticsUseCaseTest {
    @Test
    fun `calculateNumberStats counts frequencies and delays correctly`() {
        val contest1 = Contest(1, LotteryType.MEGA_SENA, "01/01/2020", listOf(1, 2, 3, 4, 5, 6))
        val contest2 = Contest(2, LotteryType.MEGA_SENA, "02/01/2020", listOf(1, 2, 3, 10, 11, 12))

        // Profile with small range for easier testing
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 12, 6, listOf(6), costPerGame = 100)

        val useCase =
            CalculateStatisticsUseCase(
                CheckGameUseCase(),
            ) // Using real one or mock? Using real one as it's simple/stateless usually. Or mock if preferred. Passed real one here.

        val stats = useCase.calculateNumberStats(listOf(contest1, contest2), profile)

        // Number 1 appeared in both contests (freq 2, delay 0)
        val stat1 = stats.find { it.number == 1 }
        assertEquals(2, stat1?.frequency)
        assertEquals(0, stat1?.delay)

        // Number 4 appeared only in contest 1 (freq 1, delay = 2 - 1 = 1)
        val stat4 = stats.find { it.number == 4 }
        assertEquals(1, stat4?.frequency)
        assertEquals(1, stat4?.delay)

        // Number 7 never appeared (freq 0, delay -1)
        val stat7 = stats.find { it.number == 7 }
        assertEquals(0, stat7?.frequency)
        assertEquals(-1, stat7?.delay)
    }

    @Test
    fun `checkHistory returns correct prize stats`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val contest = Contest(1, LotteryType.MEGA_SENA, "01/01/2020", listOf(10, 20, 30, 40, 50, 60))

        // Game matches 3 numbers (10, 20, 30)
        val selectedNumbers = listOf(10, 20, 30, 1, 2, 3)

        val useCase = CalculateStatisticsUseCase(CheckGameUseCase())
        val stats = useCase.checkHistory(selectedNumbers, listOf(contest), profile)

        // Should have 1 entry with hits=3, count=1
        assertEquals(1, stats.size)
        assertEquals(3, stats[0].hits)
        assertEquals(1, stats[0].count)
    }
}
