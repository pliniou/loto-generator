package com.cebolao.app.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cebolao.R
import com.cebolao.app.feature.statistics.components.DistributionPanel
import com.cebolao.app.feature.statistics.components.NumberFrequencyChart
import com.cebolao.app.feature.statistics.components.NumberRecencyChart
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(state.selectedType)

    CebolaoContent {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.lg)
        ) {
            // Header: Lottery Selector
            Text(
                text = stringResource(R.string.statistics_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.md))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.padding(bottom = spacing.lg),
            ) {
                items(items = LotteryType.entries, key = { it.name }) { type ->
                    val isSelected = type == state.selectedType
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onTypeSelected(type) },
                        label = { Text(stringResource(LotteryUiMapper.getNameRes(type))) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LotteryColors.getColor(type),
                            selectedLabelColor = LotteryColors.getOnColor(type)
                        )
                    )
                }
            }

            // Range Selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Analisar:", style = MaterialTheme.typography.labelLarge)
                listOf(50, 100, 200).forEach { range ->
                    FilterChip(
                        selected = state.contestRange == range,
                        onClick = { viewModel.onRangeSelected(range) },
                        label = { Text("Últimos $range") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
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
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
                        
                        // Frequency Chart
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(spacing.lg)) {
                                Text(
                                    text = "Frequência dos Números",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = lotteryColor
                                )
                                Text(
                                    text = "Quantas vezes cada número saiu nos últimos ${state.totalContestsAnalyzed} concursos.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(spacing.md))
                                NumberFrequencyChart(
                                    stats = state.numberStats,
                                    barColor = lotteryColor,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Recency Chart
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(spacing.lg)) {
                                Text(
                                    text = "Dezenas Atrasadas (Recência)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = lotteryColor
                                )
                                Text(
                                    text = "Concursos sem sair. Barras altas = números frios.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(spacing.md))
                                NumberRecencyChart(
                                    stats = state.numberStats,
                                    lineColor = lotteryColor,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Distribution
                        state.distributionStats?.let { dist ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(spacing.lg)) {
                                    DistributionPanel(stats = dist)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
