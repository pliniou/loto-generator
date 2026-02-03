package com.cebolao.domain.service

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameValidatorConfigTest {
    private val validator = GameValidator()

    @Test
    fun `multiples of 3 filter rejects all multiples or zero multiples`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val gameAllMultiples = Game("g1", LotteryType.MEGA_SENA, listOf(3, 6, 9, 12, 15, 18), createdAt = 1)
        val gameNoMultiples = Game("g2", LotteryType.MEGA_SENA, listOf(1, 2, 4, 5, 7, 8), createdAt = 1)

        assertFalse(validator.validate(gameAllMultiples, listOf(GenerationFilter.MULTIPLES_OF_3), profile))
        assertFalse(validator.validate(gameNoMultiples, listOf(GenerationFilter.MULTIPLES_OF_3), profile))
    }

    @Test
    fun `repeated previous filter rejects near identical`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val last = Contest(10, LotteryType.MEGA_SENA, "01/01/2026", listOf(1, 2, 3, 4, 5, 6))
        val gameAlmostSame = Game("g3", LotteryType.MEGA_SENA, listOf(1, 2, 3, 4, 5, 7), createdAt = 1)
        val gameDifferent = Game("g4", LotteryType.MEGA_SENA, listOf(10, 11, 12, 13, 14, 15), createdAt = 1)

        assertFalse(validator.validate(gameAlmostSame, listOf(GenerationFilter.REPEATED_FROM_PREVIOUS), profile, last))
        assertTrue(validator.validate(gameDifferent, listOf(GenerationFilter.REPEATED_FROM_PREVIOUS), profile, last))
    }

    @Test
    fun `moldura miolo filter for lotofacil requires both frame and inner`() {
        val profile = LotteryProfile(LotteryType.LOTOFACIL, "Lotofacil", 1, 25, 15, listOf(15, 14, 13, 12, 11), costPerGame = 300)
        // Somente moldura (borda) em um grid 5x5 (1..25)
        val gameFrame = Game("g5", LotteryType.LOTOFACIL, listOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24), createdAt = 1)
        // Mixed
        val gameMixed = Game("g6", LotteryType.LOTOFACIL, listOf(1, 5, 6, 7, 8, 9, 10, 11, 12, 13, 16, 17, 18, 19, 20), createdAt = 1)

        assertFalse(validator.validate(gameFrame, listOf(GenerationFilter.MOLDURA_MIOLO), profile))
        assertTrue(validator.validate(gameMixed, listOf(GenerationFilter.MOLDURA_MIOLO), profile))
    }
}
