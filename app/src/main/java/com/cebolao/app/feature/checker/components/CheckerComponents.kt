package com.cebolao.app.feature.checker.components

import android.animation.ValueAnimator
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.cebolao.domain.model.PrizeStat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.R
import com.cebolao.app.component.LotteryTypePillSelector
import com.cebolao.app.feature.checker.CheckerUiState
import com.cebolao.app.feature.checker.HistoryHit
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.NumberStat

private fun motionAwareDuration(durationMs: Int): Int {
    val reduceMotion = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()
    return if (reduceMotion) 0 else durationMs
}

@Composable
fun CheckerSectionSubheading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun CheckerTypeSelector(
    selectedType: LotteryType,
    onTypeSelected: (LotteryType) -> Unit,
) {
    val spacing = LocalSpacing.current
    LotteryTypePillSelector(
        selectedType = selectedType,
        onTypeSelected = onTypeSelected,
        contentPadding = PaddingValues(vertical = spacing.md),
    )
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
                animationSpec = tween(durationMillis = motionAwareDuration(300)),
                label = "dupla-mode-$mode",
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = motionAwareDuration(300)),
                label = "dupla-mode-text-$mode",
            )
            Surface(
                modifier = Modifier.weight(1f),
                color = modeColor,
                shape = MaterialTheme.shapes.medium,
                border =
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) modeColor else MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_FAINT),
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
                        text =
                            stringResource(
                                when (mode) {
                                    DuplaMode.FIRST -> R.string.checker_dupla_mode_first
                                    DuplaMode.SECOND -> R.string.checker_dupla_mode_second
                                    DuplaMode.BEST -> R.string.checker_dupla_mode_best
                                },
                            ),
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
    val contestInfo =
        buildString {
            append(stringResource(R.string.home_contest_number, result.contestNumber))
            if (result.contestDate.isNotBlank()) {
                append(" • ")
                append(result.contestDate)
            }
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(motionAwareDuration(240))),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = lotteryColor.copy(alpha = 0.08f)),
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
                text = "Resultado do jogo",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = pluralStringResource(R.plurals.checker_hits, result.hits, result.hits),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = lotteryColor,
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = contestInfo,
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
    NumberSelectionGrid(
        uiState = uiState,
        onNumberToggle = onNumberToggle,
        modifier = modifier,
    )
}

@Composable
fun NumberSelectionGrid(
    uiState: CheckerUiState,
    onNumberToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val fontScale = LocalDensity.current.fontScale.coerceIn(1f, 1.35f)
    val cellSize = (44f * fontScale).coerceIn(44f, 58f).dp
    val profile = uiState.profile
    val minNumber = profile?.minNumber ?: if (uiState.selectedType == LotteryType.LOTOMANIA) 0 else 1
    val maxNumber = profile?.maxNumber ?: 60
    val range = minNumber..maxNumber

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cellSize),
        contentPadding = PaddingValues(vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = modifier,
    ) {
        val list = range.toList()
        items(items = list, key = { it }) { number ->
            val isSelected = uiState.selectedNumbers.contains(number)
            val isMatched = uiState.matchedNumbers.contains(number)
            val isChecked = uiState.checkResult != null

            NumberCell(
                number = number,
                isSelected = isSelected,
                isMatched = isMatched,
                isChecked = isChecked,
                color = LotteryColors.getColor(uiState.selectedType),
                onColor = LotteryColors.getOnColor(uiState.selectedType),
                cellSize = cellSize,
                onClick = { onNumberToggle(number) },
            )
        }
    }
}

@Composable

fun NumberCell(
    number: Int,
    isSelected: Boolean,
    isMatched: Boolean,
    isChecked: Boolean,
    color: Color,
    onColor: Color,
    cellSize: Dp,
    onClick: () -> Unit,
) {
    val isMissedSelection = isSelected && isChecked && !isMatched
    val backgroundColor = when {
        isMatched -> color
        isMissedSelection -> MaterialTheme.colorScheme.errorContainer
        isSelected -> color
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor =
        when {
            isSelected || isMatched -> Color.Transparent
            else -> MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_MEDIUM)
        }
    val textColor = when {
        isMatched -> onColor
        isMissedSelection -> MaterialTheme.colorScheme.onErrorContainer
        isSelected -> onColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val hitScale by animateFloatAsState(
        targetValue = if (isChecked && isMatched) 1.08f else 1f,
        animationSpec = tween(motionAwareDuration(220)),
        label = "number-cell-scale-$number",
    )
    val checkBadgeSize = (cellSize * 0.32f).coerceAtLeast(14.dp)
    val checkIconSize = (checkBadgeSize * 0.72f).coerceAtLeast(10.dp)

    Box(
        modifier =
            Modifier
                .size(cellSize)
                .graphicsLayer {
                    scaleX = hitScale
                    scaleY = hitScale
                }
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = if (isMatched || (isSelected && isChecked)) Color.Transparent else borderColor,
                    shape = CircleShape,
                )
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(2, '0'),
            color = textColor,
            fontWeight = if (isSelected || isMatched) FontWeight.ExtraBold else FontWeight.SemiBold,
            fontSize = 15.sp,
        )

        if (isChecked && isMatched) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp, y = (-1).dp)
                        .size(checkBadgeSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(checkIconSize),
                )
            }
        }
    }
}

@Composable
fun AnalysisDialog(
    results: List<PrizeStat>,
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
                    results.forEach { stat: PrizeStat ->
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
            label = stringResource(R.string.checker_history_prize_label),
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
    prizeThreshold: Int? = null,
) {
    val spacing = LocalSpacing.current
    val winners = history.filter { it.hits > 0 }
    if (winners.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        HitsOverTimeChart(
            history = history,
            color = color,
            prizeThreshold = prizeThreshold,
        )
    }
}

@Composable
fun HitsOverTimeChart(
    history: List<HistoryHit>,
    color: Color,
    prizeThreshold: Int? = null,
) {
    val maxRenderedPoints = 800
    val renderedHistory = history.sortedBy { it.contestNumber }.takeLast(maxRenderedPoints)
    val spacing = LocalSpacing.current
    val maxHits = renderedHistory.maxOfOrNull { it.hits }?.coerceAtLeast(1) ?: 1
    val contestNumbers = renderedHistory.map { it.contestNumber }
    val minContest = contestNumbers.minOrNull() ?: 0
    val maxContest = contestNumbers.maxOrNull() ?: 0
    val contestRange = maxContest - minContest

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .animateContentSize(animationSpec = tween(motionAwareDuration(240))),
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

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small,
                            )
                            .padding(spacing.sm),
                ) {
                    // Eixo Y - Acertos
                    Column(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .width(52.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = "Acertos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
                    BoxWithConstraints(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight(1f),
                    ) {
                        val scrollState = rememberScrollState()
                        val contentWidth = maxWidth.coerceAtLeast(520.dp)
                        val chartWidth = (contentWidth - 8.dp).coerceAtLeast(1.dp)
                        val chartHeight = (maxHeight - 8.dp).coerceAtLeast(1.dp)
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .horizontalScroll(scrollState),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .width(contentWidth),
                            ) {
                                if (prizeThreshold != null && prizeThreshold in 1..maxHits) {
                                    val thresholdY =
                                        ((1f - (prizeThreshold.toFloat() / maxHits)) * chartHeight.value).coerceIn(0f, chartHeight.value)
                                    Box(
                                        modifier =
                                            Modifier
                                                .offset(y = thresholdY.dp)
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(MaterialTheme.colorScheme.tertiary),
                                    )
                                }

                                renderedHistory.forEach { hit ->
                                    if (hit.hits > 0) {
                                        val xFraction =
                                            if (contestRange > 0) {
                                                (hit.contestNumber - minContest).toFloat() / contestRange
                                            } else {
                                                0.5f
                                            }
                                        val yFraction = hit.hits.toFloat() / maxHits

                                        Box(
                                            modifier =
                                                Modifier
                                                    .offset(
                                                        x = (xFraction * chartWidth.value).coerceIn(0f, chartWidth.value).dp,
                                                        y = ((1 - yFraction) * chartHeight.value).coerceIn(0f, chartHeight.value).dp,
                                                    )
                                                    .size(8.dp)
                                                    .background(color, CircleShape),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (prizeThreshold != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Linha de premio: $prizeThreshold acertos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }

                // Legenda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "X min: concurso $minContest",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "X max: concurso $maxContest",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Linha do tempo dos concursos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                if (history.size > renderedHistory.size) {
                    Text(
                        text = "Mostrando os ultimos $maxRenderedPoints concursos para desempenho",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
    stats: List<NumberStat>,
    color: Color,
    selectedNumbers: List<Int> = emptyList(),
) {
    if (stats.isEmpty()) return
    val spacing = LocalSpacing.current
    val statsByNumber = stats.associateBy { it.number }
    val selectedStats = selectedNumbers.distinct().mapNotNull { statsByNumber[it] }
    val selectedTopFreq = selectedStats.sortedByDescending { it.frequency }
    val selectedTopDelay = selectedStats.filter { it.delay >= 0 }.sortedByDescending { it.delay }
    val topFreq = stats.sortedByDescending { it.frequency }.take(5)
    val topDelay = if (selectedTopDelay.isNotEmpty()) selectedTopDelay.take(5) else stats.filter { it.delay >= 0 }.sortedByDescending { it.delay }.take(5)
    val maxFreq = topFreq.maxOfOrNull { it.frequency }?.coerceAtLeast(1) ?: 1
    val maxDelay = topDelay.maxOfOrNull { it.delay }?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        if (selectedTopFreq.isNotEmpty()) {
            Text("Contexto das suas dezenas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            SelectedNumbersFrequencyContextChart(
                stats = selectedTopFreq,
                highlightColor = color,
            )
            Spacer(modifier = Modifier.height(spacing.md))
        }

        BoxWithConstraints {
            val showSideBySide = maxWidth > 760.dp
            if (showSideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text("Top 5 frequentes", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        NumberFrequencyChart(
                            stats = topFreq,
                            maxFreq = maxFreq,
                            color = color,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text("Atual atraso das suas dezenas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        NumberDelayChart(
                            stats = topDelay,
                            maxDelay = maxDelay,
                        )
                    }
                }
            } else {
                // Gráfico de Frequência
                Text("Top 5 frequentes", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                NumberFrequencyChart(
                    stats = topFreq,
                    maxFreq = maxFreq,
                    color = color,
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Gráfico de Atraso
                Text("Atual atraso das suas dezenas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                NumberDelayChart(
                    stats = topDelay,
                    maxDelay = maxDelay,
                )
            }
        }
    }
}

@Composable
private fun NumberFrequencyChart(
    stats: List<NumberStat>,
    maxFreq: Int,
    color: Color,
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
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
}

@Composable
private fun NumberDelayChart(
    stats: List<NumberStat>,
    maxDelay: Int,
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
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
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.width(42.dp),
            textAlign = TextAlign.Center,
        )
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
        Text(
            valueLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(92.dp),
            textAlign = TextAlign.End,
        )
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
    results: List<PrizeStat>,
    color: Color,
    prizeRanges: List<Int>,
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) return

    val spacing = LocalSpacing.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .animateContentSize(animationSpec = tween(motionAwareDuration(240))),
            shape = MaterialTheme.shapes.large,
            colors =
                CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.08f),
                ),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        ) {
            Column(
                modifier = Modifier.padding(spacing.lg),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
            Text(
                text = "Quantas vezes seus números acertariam em concursos passados:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            HitDistributionChart(
                results = results,
                color = color,
                prizeRanges = prizeRanges.toSet(),
            )

            // Total summary
            HorizontalDivider(
                modifier = Modifier.padding(top = spacing.sm),
                thickness = 0.5.dp,
                color = color.copy(alpha = 0.2f),
            )

            val totalOccurrences = results.sumOf { it.count }
            val bestHits = results.maxOfOrNull { it.hits } ?: 0

            Row(
                modifier =
                    Modifier
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
}

@Composable
fun HitDistributionChart(
    results: List<PrizeStat>,
    color: Color,
    prizeRanges: Set<Int> = emptySet(),
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) return
    val spacing = LocalSpacing.current
    val maxCount = results.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        results.sortedByDescending { it.hits }.forEach { stat ->
            val barFraction = stat.count.toFloat() / maxCount
            val isPrizeHit = stat.hits in prizeRanges
            val barColor = if (isPrizeHit) color else MaterialTheme.colorScheme.outline
            val barTextColor = if (isPrizeHit) color else MaterialTheme.colorScheme.onSurfaceVariant
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.xs),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HitCategoryMarker(
                            isWinning = isPrizeHit,
                            winningColor = color,
                        )
                        Text(
                            text = "${stat.hits} acertos",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        text = "${stat.count}x",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = barTextColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(56.dp),
                        textAlign = TextAlign.End,
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(barFraction.coerceIn(0.05f, 1f))
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            barColor.copy(alpha = if (isPrizeHit) 0.6f else 0.5f),
                                            barColor.copy(alpha = if (isPrizeHit) 0.9f else 0.75f),
                                        ),
                                    ),
                                ),
                    )
                }
            }
        }
    }
}

@Composable
private fun HitCategoryMarker(
    isWinning: Boolean,
    winningColor: Color,
) {
    Box(
        modifier =
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isWinning) winningColor else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isWinning) Color.Transparent else MaterialTheme.colorScheme.outline,
                    shape = CircleShape,
                ),
    )
}

@Composable
private fun SelectedNumbersFrequencyContextChart(
    stats: List<NumberStat>,
    highlightColor: Color,
) {
    if (stats.isEmpty()) return
    val spacing = LocalSpacing.current
    val maxFreq = stats.maxOfOrNull { it.frequency }?.coerceAtLeast(1) ?: 1
    val highlightNumbers = stats.take(5).map { it.number }.toSet()
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        ) {
            Column(
                modifier = Modifier.padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = "Todas as dezenas escolhidas (top 5 em destaque)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                stats.forEach { stat ->
                    StatBarRow(
                        label = stat.number.toString().padStart(2, '0'),
                        valueLabel = "${stat.frequency}x",
                        fraction = stat.frequency.toFloat() / maxFreq,
                        barColor = if (stat.number in highlightNumbers) highlightColor else MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
