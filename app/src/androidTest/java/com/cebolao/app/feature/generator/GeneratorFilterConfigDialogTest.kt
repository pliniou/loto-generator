package com.cebolao.app.feature.generator

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cebolao.app.feature.generator.components.GeneratorFilterConfigDialog
import com.cebolao.app.feature.generator.components.GeneratorReportDetailsDialog
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.GenerationReport
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneratorFilterConfigDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun openingFilterConfigAndApplyingPresetUpdatesCallbacks() {
        // Given a UI state with Lotofacil profile
        val profile = LotteryProfile(LotteryType.LOTOFACIL, "Lotofácil", 1, 25, 15, listOf(15, 14, 13, 12, 11), costPerGame = 300)
        val uiState = GeneratorUiState(profile = profile, showFilterConfigDialog = true)
        var applied = false
        var savedParityMin: Double? = null
        var savedParityMax: Double? = null
        var savedRepeats: Int? = null
        var toggledFilter: GenerationFilter? = null
        val savedPresetName: String? = null
        val appliedUserPreset: String? = null
        val deletedUserPreset: String? = null

        composeTestRule.setContent {
            // Wrap in theme scaffold if necessary
            androidx.compose.material3.MaterialTheme {
                GeneratorFilterConfigDialog(
                    uiState = uiState,
                    onClose = {},
                    onApplyPreset = { applied = true },
                    onUpdateFilterConfig = { filter, cfg ->
                        if (filter == GenerationFilter.PARITY_BALANCE) {
                            savedParityMin = cfg.minParityRatio
                            savedParityMax = cfg.maxParityRatio
                        }
                        if (filter == GenerationFilter.REPEATED_FROM_PREVIOUS) savedRepeats = cfg.maxRepeatsFromPrevious
                    },
                    onToggleFilter = { toggledFilter = it },
                )
            }
        }

        // Apply preset
        composeTestRule.onNodeWithText("Aplicar preset: Lotofácil").performClick()
        assert(applied)

        // Toggle parity filter checkbox
        composeTestRule.onNodeWithTag("filter_toggle_PARITY_BALANCE").performClick()
        assert(toggledFilter == GenerationFilter.PARITY_BALANCE)

        // Enter parity values and save
        composeTestRule.onNodeWithText("Paridade mínima (ex: 0.2)").performTextInput("0.33")
        composeTestRule.onNodeWithText("Paridade máxima (ex: 0.8)").performTextInput("0.66")
        composeTestRule.onNodeWithText("Salvar").performClick()

        // Values propagated through callback
        assert(savedParityMin == 0.33)
        assert(savedParityMax == 0.66)

        // Save user preset
        composeTestRule.onNodeWithText("Nome do preset").performTextInput("meuPresets")
        composeTestRule.onNodeWithText("Salvar preset").performClick()
        assert(savedPresetName == "meuPresets")

        // Apply user preset
        composeTestRule.onNodeWithText("Aplicar").performClick()
        assert(appliedUserPreset == "u1")

        // Delete user preset
        composeTestRule.onNodeWithText("Excluir").performClick()
        assert(deletedUserPreset == "u1")
    }

    @Test
    fun reportDetailsDialogShowsBreakdownAndExamples() {
        val report =
            GenerationReport(
                attempts = 10,
                generated = 2,
                rejectedByFilter = 5,
                rejectedPerFilter = mapOf(GenerationFilter.PARITY_BALANCE to 5),
                rejectedExamples = mapOf(GenerationFilter.PARITY_BALANCE to listOf(listOf(2, 4, 6, 8, 10, 12))),
                partial = true,
            )
        val uiState = GeneratorUiState(generationReport = report, showReportDialog = true)

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                GeneratorReportDetailsDialog(uiState = uiState, onClose = {})
            }
        }

        composeTestRule.onNodeWithText("Detalhes do relatório").assertExists()
        composeTestRule.onNodeWithText("Tentativas: 10").assertExists()
        composeTestRule.onNodeWithText("- PARITY_BALANCE: 5").assertExists()
        composeTestRule.onNodeWithText("Exemplo:").assertExists()
        composeTestRule.onNodeWithText("2, 4, 6, 8, 10, 12").assertExists()
    }
}
