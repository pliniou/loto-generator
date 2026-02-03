package com.cebolao.domain.usecase

import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.GenerationConfig
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.service.GameValidator
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateGamesUseCaseExamplesTest {
    @Test
    fun `generator records examples of rejected games per filter`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val cfg =
            GenerationConfig(
                quantity = 5,
                filters = listOf(GenerationFilter.PARITY_BALANCE),
                filterConfigs = mapOf(GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.0, maxParityRatio = 0.1)),
            )

        val useCase = GenerateGamesUseCase(GameValidator())
        val result = useCase(profile, cfg, maxRetry = 100, random = kotlin.random.Random(2))

        assertTrue(result.report.rejectedByFilter > 0)
        val examples = result.report.rejectedExamples[GenerationFilter.PARITY_BALANCE]
        assertNotNull(examples)
        assertTrue(examples!!.isNotEmpty())
        assertTrue(examples.all { it.size == profile.numbersPerGame })
    }
}
