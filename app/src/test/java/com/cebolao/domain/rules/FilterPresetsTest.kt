package com.cebolao.domain.rules

import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FilterPresetsTest {
    @Test
    fun `presets include quina timemania and super sete`() {
        val q = FilterPresets.presetForProfile(LotteryProfile(LotteryType.QUINA, "Quina", 1, 80, 5, listOf(5, 4, 3), costPerGame = 250))
        val t =
            FilterPresets.presetForProfile(
                LotteryProfile(LotteryType.TIMEMANIA, "Timemania", 1, 80, 10, listOf(7, 6, 5), costPerGame = 300),
            )
        val s = FilterPresets.presetForProfile(LotteryProfile(LotteryType.SUPER_SETE, "Super", 0, 9, 7, listOf(7, 6, 5), costPerGame = 250))

        assertNotNull(q)
        assertNotNull(t)
        assertNotNull(s)

        assertEquals("Quina", q?.name)
        assertEquals("Timemania", t?.name)
        assertEquals("Super Sete", s?.name)
    }
}
