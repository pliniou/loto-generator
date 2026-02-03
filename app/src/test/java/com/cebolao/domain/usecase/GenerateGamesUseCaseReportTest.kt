package com.cebolao.domain.usecase

import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.GenerationConfig
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.service.GameValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateGamesUseCaseReportTest {
    @Test
    fun `generator reports rejected counts per filter`() {
        val profile = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 100)
        val config =
            GenerationConfig(
                quantity = 10,
                filters = listOf(GenerationFilter.PARITY_BALANCE),
                filterConfigs = mapOf(GenerationFilter.PARITY_BALANCE to FilterConfig(minParityRatio = 0.0, maxParityRatio = 0.1)),
            )

        val useCase = GenerateGamesUseCase(GameValidator())
        val result =
            useCase(
                profile,
                config,
                lastContest = null,
                maxRetry = 50,
                random = kotlin.random.Random(1),
            )

        // Quando o maxRetry é pequeno e o filtro é muito restritivo, esperamos rejeições por paridade
        assertTrue(result.report.rejectedByFilter > 0)
        assertTrue((result.report.rejectedPerFilter[GenerationFilter.PARITY_BALANCE] ?: 0) > 0)
        assertEquals(result.report.rejectedByFilter, result.report.rejectedPerFilter.values.sum())
    }
}
