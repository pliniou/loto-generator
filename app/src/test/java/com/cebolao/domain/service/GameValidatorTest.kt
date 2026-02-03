package com.cebolao.domain.service

import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameValidatorTest {
    @Test
    fun `parity filter rejects extreme maps`() {
        val game = Game(id = "g", lotteryType = LotteryType.MEGA_SENA, numbers = List(12) { it * 2 + 2 }, createdAt = 1)
        val validator = GameValidator()
        // Mock profile needed for parity check if required by validator, assuming default implies parity check if passed in filters
        // But wait, validate takes filters list. If empty, it passes.
        // We need to pass PARITY_BALANCE filter.
        // And we need a profile.
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6), costPerGame = 100)

        val valid = validator.validate(game, listOf(GenerationFilter.PARITY_BALANCE), profile)
        assertFalse(valid)
    }

    @Test
    fun `super sete bypasses parity filter`() {
        val game = Game(id = "g2", lotteryType = LotteryType.SUPER_SETE, numbers = List(7) { 0 }, createdAt = 1)
        val validator = GameValidator()
        val profile = LotteryProfile(LotteryType.SUPER_SETE, "Super", 0, 9, 7, listOf(7), costPerGame = 100)

        val valid = validator.validate(game, listOf(GenerationFilter.PARITY_BALANCE), profile)
        assertTrue(valid)
    }
}
