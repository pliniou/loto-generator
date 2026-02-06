package com.cebolao.app.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.LotteryFilterBar
import com.cebolao.app.feature.statistics.components.DistributionPanel
import com.cebolao.app.feature.statistics.components.NumberFrequencyChart
import com.cebolao.app.feature.statistics.components.NumberRecencyChart
import com.cebolao.app.feature.statistics.components.QuadrantsPanel
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper

private const val WIDE_WIDTH_BREAKPOINT_DP = 720

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(state.selectedType)
    val lotteryName = stringResource(LotteryUiMapper.getNameRes(state.selectedType))
    val rangeLabel =
        if (state.totalContestsAnalyzed > 0) {
            "nos últimos ${state.totalContestsAnalyzed} concursos"
        } else {
            "nos últimos ${state.contestRange} concursos"
        }

    CebolaoContent {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.lg),
        ) {
            // Header: Lottery Selector
            Text(
                text = stringResource(R.string.statistics_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(spacing.md))

            LotteryFilterBar(
                selectedType = state.selectedType,
                onSelectionChanged = { type -> type?.let(viewModel::onTypeSelected) },
                showSelectedCheck = true,
                modifier = Modifier.padding(bottom = spacing.lg),
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.lg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.md)) {
                    Text(
                        text = "Período analisado",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        listOf(50, 100, 200).forEach { range ->
                            FilterChip(
                                selected = state.contestRange == range,
                                onClick = { viewModel.onRangeSelected(range) },
                                label = { Text("Últimos $range") },
                                colors =
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = "$lotteryName • $rangeLabel",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = lotteryColor)
                }
            } else {
                AnimatedVisibility(
                    visible = !state.isLoading,
                    enter = fadeIn() + expandVertically(),
                ) {
                    Crossfade(
                        targetState = Pair(state.selectedType, state.contestRange),
                        animationSpec = tween(durationMillis = 250),
                        label = "statistics_crossfade",
                    ) { statisticsState ->
                        key(statisticsState) {
                            BoxWithConstraints {
                                val isWide = maxWidth >= WIDE_WIDTH_BREAKPOINT_DP.dp
                                Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
                                    if (isWide) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            FrequencyCard(
                                                state = state,
                                                lotteryColor = lotteryColor,
                                                lotteryName = lotteryName,
                                                rangeLabel = rangeLabel,
                                                modifier = Modifier.weight(1f),
                                            )
                                            RecencyCard(
                                                state = state,
                                                lotteryColor = lotteryColor,
                                                rangeLabel = rangeLabel,
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                    } else {
                                        FrequencyCard(
                                            state = state,
                                            lotteryColor = lotteryColor,
                                            lotteryName = lotteryName,
                                            rangeLabel = rangeLabel,
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                        RecencyCard(
                                            state = state,
                                            lotteryColor = lotteryColor,
                                            rangeLabel = rangeLabel,
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }

                                    // Distribution
                                    state.distributionStats?.let { dist ->
                                        if (isWide) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                                modifier = Modifier.fillMaxWidth(),
                                            ) {
                                                DistributionCard(dist = dist, modifier = Modifier.weight(1f))
                                                QuadrantsCard(dist = dist, modifier = Modifier.weight(1f))
                                            }
                                        } else {
                                            DistributionCard(dist = dist, modifier = Modifier.fillMaxWidth())
                                            QuadrantsCard(dist = dist, modifier = Modifier.fillMaxWidth())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistributionCard(
    dist: com.cebolao.domain.model.DistributionStats,
    modifier: Modifier,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            DistributionPanel(stats = dist)
        }
    }
}

@Composable
private fun QuadrantsCard(
    dist: com.cebolao.domain.model.DistributionStats,
    modifier: Modifier,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            QuadrantsPanel(quadrants = dist.quadrantDistribution)
        }
    }
}

@Composable
private fun FrequencyCard(
    state: StatisticsUiState,
    lotteryColor: Color,
    lotteryName: String,
    rangeLabel: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            Text(
                text = "Frequência das dezenas — $rangeLabel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = lotteryColor,
            )
            Text(
                text = "Dados de $lotteryName. Destaques mostram as mais quentes e frias no período.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.numberStats.isNotEmpty()) {
                val hottest = state.highlights.maxFrequencyNumbers.firstOrNull()
                Spacer(modifier = Modifier.height(spacing.xs))
                hottest?.let {
                    Text(
                        text = "Número que mais saiu: $it (${state.highlights.maxFrequencyValue} vezes)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text =
                        "Mais frequentes (${state.highlights.maxFrequencyValue}x): ${formatNumberList(state.highlights.maxFrequencyNumbers)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text =
                        "Menos frequentes (${state.highlights.minFrequencyValue}x): ${formatNumberList(state.highlights.minFrequencyNumbers)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(spacing.md))
            NumberFrequencyChart(
                stats = state.numberStats,
                barColor = lotteryColor,
                highlightMaxNumbers = state.highlights.maxFrequencyNumbers.toSet(),
                highlightMinNumbers = state.highlights.minFrequencyNumbers.toSet(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RecencyCard(
    state: StatisticsUiState,
    lotteryColor: Color,
    rangeLabel: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            Text(
                text = "Dezenas atrasadas — $rangeLabel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = lotteryColor,
            )
            Text(
                text = "Barras discretas por número. Toque em uma barra para ver há quantos concursos ela não aparece.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.numberStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.xs))
                val delayLabel =
                    if (state.highlights.maxDelayValue < 0) {
                        "nunca saiu neste recorte"
                    } else {
                        "há ${state.highlights.maxDelayValue} concursos"
                    }
                val mostDelayed = state.highlights.maxDelayNumbers.firstOrNull()
                mostDelayed?.let {
                    Text(
                        text = "Número mais atrasado: $it ($delayLabel)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text =
                        "Mais atrasadas: ${formatNumberList(state.highlights.maxDelayNumbers)} ($delayLabel)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(spacing.md))
            NumberRecencyChart(
                stats = state.numberStats,
                barColor = lotteryColor,
                highlightColor = MaterialTheme.colorScheme.error,
                highlightedNumbers = state.highlights.topDelayNumbers.toSet(),
                topLabeledNumbers = state.highlights.topDelayNumbers.toSet(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun formatNumberList(numbers: List<Int>, limit: Int = 6): String {
    if (numbers.isEmpty()) return "-"
    val trimmed = numbers.take(limit)
    val suffix = if (numbers.size > limit) "..." else ""
    return trimmed.joinToString(", ") + suffix
}
