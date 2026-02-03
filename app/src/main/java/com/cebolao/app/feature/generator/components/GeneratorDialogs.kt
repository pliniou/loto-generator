package com.cebolao.app.feature.generator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.feature.generator.GeneratorUiState
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.util.GenerationFilterUiMapper
import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.rules.FilterPresets
import kotlin.math.roundToInt

@Composable
fun GeneratorFilterConfigDialog(
    uiState: GeneratorUiState,
    onClose: () -> Unit,
    onApplyPreset: () -> Unit,
    onUpdateFilterConfig: (GenerationFilter, FilterConfig) -> Unit,
    onToggleFilter: (GenerationFilter) -> Unit,
) {
    val spacing = LocalSpacing.current

    // Estados locais para os Sliders (inicializados com valores da config)
    val currentParityCfg = uiState.filterConfigs[GenerationFilter.PARITY_BALANCE] ?: FilterConfig()
    var parityRange by remember {
        mutableStateOf(
            (currentParityCfg.minParityRatio?.toFloat() ?: 0.2f)..(currentParityCfg.maxParityRatio?.toFloat() ?: 0.8f),
        )
    }

    val currentRepeatCfg = uiState.filterConfigs[GenerationFilter.REPEATED_FROM_PREVIOUS] ?: FilterConfig()
    var maxRepeats by remember { mutableFloatStateOf(currentRepeatCfg.maxRepeatsFromPrevious?.toFloat() ?: 4f) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = {
                // Ao confirmar, atualiza o ViewModel e fecha
                onUpdateFilterConfig(
                    GenerationFilter.PARITY_BALANCE,
                    FilterConfig(minParityRatio = parityRange.start.toDouble(), maxParityRatio = parityRange.endInclusive.toDouble()),
                )
                onUpdateFilterConfig(
                    GenerationFilter.REPEATED_FROM_PREVIOUS,
                    FilterConfig(maxRepeatsFromPrevious = maxRepeats.toInt()),
                )
                onClose()
            }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text(stringResource(R.string.action_cancel)) }
        },
        title = { Text(stringResource(R.string.config_filters)) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // --- Seção: Presets ---
                Text(
                    text = stringResource(R.string.generator_presets_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                val preset = uiState.profile?.let { FilterPresets.presetForProfile(it) }
                preset?.let { p ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = spacing.sm),
                    ) {
                        Text(text = p.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(text = p.description ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(spacing.sm))
                        OutlinedButton(
                            onClick = { onApplyPreset() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.apply_preset, p.name))
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_FAINT))

                // --- Seção: Ativação de Filtros ---
                Text(
                    text = stringResource(R.string.generator_filters_active),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                val applicableFilters =
                    listOf(
                        GenerationFilter.PARITY_BALANCE,
                        GenerationFilter.MULTIPLES_OF_3,
                        GenerationFilter.REPEATED_FROM_PREVIOUS,
                        GenerationFilter.MOLDURA_MIOLO,
                        GenerationFilter.PRIME_NUMBERS,
                    ).filter { it.isApplicable(uiState.profile ?: return@filter true) }

                applicableFilters.forEach { filter ->
                    val isActive = uiState.activeFilters.contains(filter)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("filter_toggle_${filter.name}"),
                    ) {
                        Checkbox(checked = isActive, onCheckedChange = { onToggleFilter(filter) })
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Column {
                            Text(
                                text = stringResource(GenerationFilterUiMapper.getLabelRes(filter)),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(GenerationFilterUiMapper.getDescriptionRes(filter)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_FAINT))

                // --- Seção: Ajuste Fino (Sliders) ---
                Text(
                    text = stringResource(R.string.generator_fine_tuning),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                // Paridade Slider
                Column {
                    Text(
                        text =
                            stringResource(
                                R.string.filter_parity_range,
                                (parityRange.start * 100).roundToInt(),
                                (parityRange.endInclusive * 100).roundToInt(),
                            ),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    RangeSlider(
                        value = parityRange,
                        onValueChange = { parityRange = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Repetições Slider
                val maxPossibleRepeats = uiState.profile?.numbersPerGame?.toFloat() ?: 15f
                Column {
                    Text(
                        text = stringResource(R.string.filter_repeats_limit, maxRepeats.roundToInt()),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Slider(
                        value = maxRepeats,
                        onValueChange = { maxRepeats = it },
                        valueRange = 0f..maxPossibleRepeats,
                        steps = maxPossibleRepeats.toInt() - 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )
}

@Composable
fun GeneratorReportDetailsDialog(
    uiState: GeneratorUiState,
    onClose: () -> Unit,
) {
    val report = uiState.generationReport ?: return
    val spacing = LocalSpacing.current

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text(stringResource(R.string.action_close)) } },
        title = { Text(stringResource(R.string.report_details)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Text(
                    text = stringResource(R.string.report_attempts, report.attempts),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))

                Text(text = stringResource(R.string.report_rejected_by_filter), style = MaterialTheme.typography.titleSmall)

                val maxCount = (report.rejectedPerFilter.values.maxOrNull() ?: 1).coerceAtLeast(1)

                report.rejectedPerFilter.forEach { (filter, count) ->
                    val fraction = count.toFloat() / maxCount.toFloat()

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Text(
                            text = stringResource(GenerationFilterUiMapper.getLabelRes(filter)),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { fraction },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(8.dp),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Spacer(modifier = Modifier.width(spacing.md))
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        },
    )
}

