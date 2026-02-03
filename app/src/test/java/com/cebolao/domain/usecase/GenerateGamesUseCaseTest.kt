package com.cebolao.domain.usecase

import com.cebolao.domain.model.GenerationConfig
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.service.GameValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateGamesUseCaseTest {
    @Test
    fun `partial generation when unique combos exhausted`() {
        // Profile constrained: numbers 1..6, pick 6 => only 1 unique game possible
        val profile =
            LotteryProfile(
                // type not relevant here
                type = LotteryType.MEGA_SENA,
                name = "Test",
                minNumber = 1,
                maxNumber = 6,
                numbersPerGame = 6,
                prizeRanges = listOf(6, 5),
                costPerGame = 100,
            )

        val config = GenerationConfig(quantity = 2)

        val useCase = GenerateGamesUseCase(GameValidator())
        val result = useCase(profile, config, maxRetry = 100, random = kotlin.random.Random(42))

        // Only one unique can be generated
        assertTrue(result.report.partial)
        assertEquals(1, result.report.generated)
        assertEquals(1, result.games.size)
    }
}
