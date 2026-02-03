package com.cebolao.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerationFilterTest {
    @Test
    fun `moldura miolo aplica apenas para lotofacil`() {
        val lotofacil = LotteryProfile(LotteryType.LOTOFACIL, "Lotofácil", 1, 25, 15, listOf(15, 14, 13), costPerGame = 300)
        val mega = LotteryProfile(LotteryType.MEGA_SENA, "Mega", 1, 60, 6, listOf(6, 5, 4), costPerGame = 600)

        assertTrue(GenerationFilter.MOLDURA_MIOLO.isApplicable(lotofacil))
        assertFalse(GenerationFilter.MOLDURA_MIOLO.isApplicable(mega))
    }

    @Test
    fun `paridade e multiplos nao se aplicam ao super sete`() {
        val superSete =
            LotteryProfile(LotteryType.SUPER_SETE, "Super Sete", 0, 9, 7, listOf(7, 6, 5), isSuperSete = true, costPerGame = 300)

        assertFalse(GenerationFilter.PARITY_BALANCE.isApplicable(superSete))
        assertFalse(GenerationFilter.MULTIPLES_OF_3.isApplicable(superSete))
    }
}
