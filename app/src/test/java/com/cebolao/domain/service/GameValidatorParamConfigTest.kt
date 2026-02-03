package com.cebolao.domain.service

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameValidatorParamConfigTest {
    private val validator = GameValidator()

    @Test
    fun `parity filter respects configured thresholds`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        // Game with 5 evens out of 6 => ratio 0.8333
        val game = Game("g1", LotteryType.MEGA_SENA, listOf(2, 4, 6, 8, 10, 11), createdAt = 1)

        // Default should reject (default max 0.8)
        assertFalse(validator.validate(game, listOf(GenerationFilter.PARITY_BALANCE), profile))

        // With custom config accepting up to 0.85 should pass
        val cfg = FilterConfig(minParityRatio = 0.0, maxParityRatio = 0.85)
        assertTrue(
            validator.validate(
                game,
                listOf(GenerationFilter.PARITY_BALANCE),
                profile,
                configs = mapOf(GenerationFilter.PARITY_BALANCE to cfg),
            ),
        )
    }

    @Test
    fun `repeats filter respects configured max repeats`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val last = Contest(10, LotteryType.MEGA_SENA, "01/01/2026", listOf(1, 2, 3, 4, 5, 6))
        // Game that repeats 5 numbers
        val game = Game("g2", LotteryType.MEGA_SENA, listOf(1, 2, 3, 4, 5, 7), createdAt = 1)

        // Default: game size 6 => default maxRepeats = size - 2 = 4, so reject
        assertFalse(validator.validate(game, listOf(GenerationFilter.REPEATED_FROM_PREVIOUS), profile, last))

        // With config that allows 5 repeats
        val cfg = FilterConfig(maxRepeatsFromPrevious = 5)
        assertTrue(
            validator.validate(
                game,
                listOf(GenerationFilter.REPEATED_FROM_PREVIOUS),
                profile,
                last,
                configs = mapOf(GenerationFilter.REPEATED_FROM_PREVIOUS to cfg),
            ),
        )
    }
}
