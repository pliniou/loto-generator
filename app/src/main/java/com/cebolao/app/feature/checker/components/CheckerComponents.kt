package com.cebolao.app.feature.checker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.R
import com.cebolao.app.feature.checker.CheckerUiState
import com.cebolao.app.feature.checker.HistoryHit
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.LotteryType

@Composable
fun CheckerTypeSelector(
    selectedType: LotteryType,
    onTypeSelected: (LotteryType) -> Unit,
) {
    val spacing = LocalSpacing.current
    LazyRow(
        contentPadding = PaddingValues(vertical = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        items(items = LotteryType.entries, key = { it.name }) { type ->
            val isSelected = type == selectedType
            val chipColor by animateColorAsState(
                targetValue = LotteryColors.getColor(type),
                animationSpec = tween(durationMillis = 300),
                label = "checker-chip-$type"
            )
            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                label = { Text(stringResource(LotteryUiMapper.getNameRes(type))) },
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipColor,
                        selectedLabelColor = LotteryColors.getOnColor(type),
                    ),
                border =
                    if (isSelected) {
                        null
                    } else {
                        FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = chipColor.copy(alpha = AlphaLevels.BORDER_MEDIUM),
                        )
                    },
            )
        }
    }
}

@Composable
fun CheckerDuplaModeSelector(
    selectedMode: DuplaMode,
    onModeSelected: (DuplaMode) -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.padding(bottom = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        DuplaMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            val modeColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(durationMillis = 300),
                label = "dupla-mode-$mode"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 300),
                label = "dupla-mode-text-$mode"
            )
            Surface(
                modifier = Modifier.weight(1f),
                color = modeColor,
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) modeColor else MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_FAINT)
                ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onModeSelected(mode) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(mode.nameRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                        modifier = Modifier.padding(vertical = spacing.xs),
                    )
                }
            }
        }
    }
}

@Composable
fun CheckerResultCard(uiState: CheckerUiState) {
    val result = uiState.checkResult ?: return
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(uiState.selectedType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = lotteryColor.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, lotteryColor.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(spacing.lg)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = pluralStringResource(R.plurals.checker_hits, result.hits, result.hits),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = lotteryColor,
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = stringResource(R.string.home_contest_number, result.contestNumber),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (result.isPrize || result.teamHit != null) {
                Spacer(modifier = Modifier.height(spacing.md))
                HorizontalDivider(thickness = 0.5.dp, color = lotteryColor.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(spacing.md))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Selo de premiação
                    if (result.isPrize) {
                        Surface(
                            color = lotteryColor,
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                text = stringResource(R.string.checker_prize_badge),
                                color = LotteryColors.getOnColor(uiState.selectedType),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                            )
                        }
                    }

                    // Resultado do time
                    if (result.teamHit != null) {
                        Surface(
                            color = if (result.teamHit) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                text =
                                    if (result.teamHit) {
                                        stringResource(
                                            R.string.timemania_team_hit,
                                        )
                                    } else {
                                        stringResource(R.string.timemania_team_miss)
                                    },
                                color = if (result.teamHit) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onError,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckerNumberGrid(
    uiState: CheckerUiState,
    onNumberToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val profile = uiState.profile
    val minNumber = profile?.minNumber ?: if (uiState.selectedType == LotteryType.LOTOMANIA) 0 else 1
    val maxNumber = profile?.maxNumber ?: 60
    val range = minNumber..maxNumber

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 44.dp),
        contentPadding = PaddingValues(vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = modifier,
    ) {
        val list = range.toList()
        items(list.size) { index ->
            val number = list[index]
            val isSelected = uiState.selectedNumbers.contains(number)
            NumberCell(
                number = number,
                isSelected = isSelected,
                color = LotteryColors.getColor(uiState.selectedType),
                onColor = LotteryColors.getOnColor(uiState.selectedType),
                onClick = { onNumberToggle(number) },
            )
        }
    }
}

@Composable
fun NumberCell(
    number: Int,
    isSelected: Boolean,
    color: Color,
    onColor: Color,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = CircleShape,
                )
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = if (isSelected) onColor else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = 15.sp,
        )
    }
}

@Composable
fun AnalysisDialog(
    results: List<com.cebolao.domain.model.PrizeStat>,
    onDismiss: () -> Unit,
) {
    val spacing = LocalSpacing.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checker_analysis_title), fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                if (results.isEmpty()) {
                    Text(stringResource(R.string.checker_analysis_empty))
                } else {
                    Text(
                        stringResource(R.string.checker_analysis_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    results.forEach { stat ->
                        Column {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.sm),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = pluralStringResource(R.plurals.checker_hits, stat.hits, stat.hits),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = CircleShape,
                                ) {
                                    Text(
                                        text = "${stat.count}x",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok), fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Composable
fun HistorySummaryRow(
    total: Int,
    best: Int,
    prizeCount: Int,
    color: Color,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        SummaryChip(
            label = "Concursos",
            value = total.toString(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.weight(1f),
        )
        SummaryChip(label = "Melhor acerto", value = best.toString(), color = color.copy(alpha = 0.15f), modifier = Modifier.weight(1f))
        SummaryChip(
            label = "Premiações",
            value = prizeCount.toString(),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun HistoryHitsList(
    history: List<HistoryHit>,
    color: Color,
) {
    val spacing = LocalSpacing.current
    val winners = history.filter { it.hits > 0 }
    if (winners.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text("Onde você mais acertou", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

        // Gráfico de dispersão (x,y)
        HistoryHitsChart(
            history = history,
            color = color,
        )
    }
}

@Composable
private fun HistoryHitsChart(
    history: List<HistoryHit>,
    color: Color,
) {
    val spacing = LocalSpacing.current
    val maxHits = history.maxOfOrNull { it.hits }?.coerceAtLeast(1) ?: 1
    val contestNumbers = history.map { it.contestNumber }
    val minContest = contestNumbers.minOrNull() ?: 0
    val maxContest = contestNumbers.maxOrNull() ?: 0
    val contestRange = maxContest - minContest

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
        ) {
            Text(
                text = "Distribuição de Acertos por Concurso",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(spacing.sm),
                contentAlignment = Alignment.CenterStart,
            ) {
                // Eixo Y - Acertos
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = maxHits.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Gráfico de pontos
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp),
                ) {
                    history.forEach { hit ->
                        if (hit.hits > 0) {
                            val xFraction =
                                if (contestRange > 0) {
                                    (hit.contestNumber - minContest).toFloat() / contestRange
                                } else {
                                    hit.contestNumber.toFloat() / maxContest
                                }
                            val yFraction = hit.hits.toFloat() / maxHits

                            Box(
                                modifier =
                                    Modifier
                                        .offset(
                                            x = (xFraction * 300f).coerceIn(0f, 300f).dp,
                                            y = ((1 - yFraction) * 130f).coerceIn(0f, 130f).dp,
                                        )
                                        .size(8.dp)
                                        .background(color, CircleShape),
                            )
                        }
                    }
                }
            }

            // Legenda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Concurso mais antigo: $minContest",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Concurso mais recente: $maxContest",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HistoryHitRow(
    hit: HistoryHit,
    color: Color,
) {
    val spacing = LocalSpacing.current
    val barFraction = (hit.hits.coerceAtLeast(1)).toFloat() / 15f // 15 é máx Lotofácil; outras ainda ok
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.width(86.dp)) {
            Text("Concurso ${hit.contestNumber}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(hit.contestDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(barFraction)
                        .clip(MaterialTheme.shapes.small)
                        .background(color),
            )
        }
        Text("${hit.hits}x", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@Composable
fun NumberStatsSection(
    stats: List<com.cebolao.domain.model.NumberStat>,
    color: Color,
) {
    if (stats.isEmpty()) return
    val spacing = LocalSpacing.current
    val topFreq = stats.sortedByDescending { it.frequency }.take(5)
    val topDelay = stats.filter { it.delay >= 0 }.sortedByDescending { it.delay }.take(5)
    val maxFreq = topFreq.maxOfOrNull { it.frequency }?.coerceAtLeast(1) ?: 1
    val maxDelay = topDelay.maxOfOrNull { it.delay }?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        // Gráfico de Frequência
        Text("Frequência das dezenas (top 5)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        NumberFrequencyChart(
            stats = topFreq,
            maxFreq = maxFreq,
            color = color,
        )

        Spacer(modifier = Modifier.height(spacing.md))

        // Gráfico de Atraso
        Text("Dezenas mais atrasadas (top 5)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        NumberDelayChart(
            stats = topDelay,
            maxDelay = maxDelay,
        )
    }
}

@Composable
private fun NumberFrequencyChart(
    stats: List<com.cebolao.domain.model.NumberStat>,
    maxFreq: Int,
    color: Color,
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
        ) {
            // Eixo Y - Frequência
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .padding(end = spacing.sm),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = maxFreq.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Barras
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    stats.forEach { stat ->
                        StatBarRow(
                            label = stat.number.toString().padStart(2, '0'),
                            valueLabel = "${stat.frequency}x",
                            fraction = stat.frequency.toFloat() / maxFreq,
                            barColor = color,
                        )
                    }
                }
            }

            // Legenda do eixo X
            Text(
                text = "Dezenas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun NumberDelayChart(
    stats: List<com.cebolao.domain.model.NumberStat>,
    maxDelay: Int,
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
        ) {
            // Eixo Y - Atraso
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .padding(end = spacing.sm),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "$maxDelay conc.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Barras
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    stats.forEach { stat ->
                        StatBarRow(
                            label = stat.number.toString().padStart(2, '0'),
                            valueLabel = "${stat.delay} concursos",
                            fraction = stat.delay.toFloat() / maxDelay,
                            barColor = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Legenda do eixo X
            Text(
                text = "Dezenas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatBarRow(
    label: String,
    valueLabel: String,
    fraction: Float,
    barColor: Color,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(40.dp))
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .clip(MaterialTheme.shapes.small)
                        .background(barColor.copy(alpha = 0.8f)),
            )
        }
        Text(valueLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Surface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape,
    color: Color,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        content = content,
    )
}

/**
 * Seção de análise estatística inline - exibe resultados diretamente na tela
 * em vez de popup/dialog para melhor UX
 */
@Composable
fun AnalysisStatsSection(
    results: List<com.cebolao.domain.model.PrizeStat>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) return

    val spacing = LocalSpacing.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f),
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Análise de Acertos Históricos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                )
            }

            Text(
                text = "Quantas vezes seus números acertariam em concursos passados:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // Stats Grid
            val maxCount = results.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

            results.sortedByDescending { it.hits }.forEach { stat ->
                val barFraction = stat.count.toFloat() / maxCount

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Hits badge
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(color.copy(alpha = 0.15f))
                            .padding(horizontal = spacing.sm, vertical = spacing.xs),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${stat.hits} acertos",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = color,
                        )
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(barFraction.coerceIn(0.05f, 1f))
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            color.copy(alpha = 0.6f),
                                            color.copy(alpha = 0.9f),
                                        )
                                    )
                                ),
                        )
                    }

                    // Count
                    Text(
                        text = "${stat.count}x",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                        modifier = Modifier.width(48.dp),
                        textAlign = TextAlign.End,
                    )
                }
            }

            // Total summary
            HorizontalDivider(
                modifier = Modifier.padding(top = spacing.sm),
                thickness = 0.5.dp,
                color = color.copy(alpha = 0.2f),
            )

            val totalOccurrences = results.sumOf { it.count }
            val bestHits = results.maxOfOrNull { it.hits } ?: 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.sm),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${results.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "faixas",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalOccurrences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = "ocorrências",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$bestHits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                    )
                    Text(
                        text = "melhor",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
