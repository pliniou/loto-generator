package com.cebolao.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.LotteryCard
import com.cebolao.app.component.WelcomeBanner
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToChecker: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    CebolaoContent {
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = spacing.lg),
            )

            if (uiState.isSyncing) {
                LinearProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(top = spacing.lg, bottom = spacing.xxl),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing.md),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    WelcomeBanner(modifier = Modifier.padding(bottom = spacing.xs))
                }

                // Seção de Próximos Concursos e Previsões
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Text(
                                text = stringResource(R.string.home_schedule_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            LotteryType.entries.forEach { type ->
                                val contest = uiState.contests[type]
                                val lotteryColor = LotteryColors.getColor(type)

                                if (contest != null) {
                                    val estimatedPrize = contest.nextContestEstimatedPrize ?: 0.0
                                    val hasPrize = estimatedPrize > 0

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(LotteryUiMapper.getNameRes(type)),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = lotteryColor,
                                            )
                                            Text(
                                                text = "Próximo: Conc. ${contest.id + 1}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }

                                        if (hasPrize) {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                val formattedPrize = when {
                                                    estimatedPrize >= 1_000_000_000 -> "R$ ${String.format("%.1f", estimatedPrize / 1_000_000_000.0)}B"
                                                    estimatedPrize >= 1_000_000 -> "R$ ${String.format("%.1f", estimatedPrize / 1_000_000.0)}M"
                                                    estimatedPrize >= 1_000 -> "R$ ${String.format("%.0f", estimatedPrize / 1_000.0)}K"
                                                    else -> "R$ ${estimatedPrize.toLong()}"
                                                }
                                                Text(
                                                    text = formattedPrize,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = lotteryColor,
                                                )
                                                Text(
                                                    text = "Prêmio est.",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        }
                                    }

                                    if (type != LotteryType.entries.last()) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = spacing.xs),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(items = LotteryType.entries, key = { it.name }) { type ->
                    val contest = uiState.contests[type]
                    LotteryCard(
                        contest = contest,
                        lotteryType = type,
                        onClick = onNavigateToChecker,
                    )
                }
                item { Spacer(modifier = Modifier.height(spacing.xxl)) }
            }

            FloatingActionButton(
                onClick = { viewModel.refreshData() },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = spacing.xl, end = spacing.lg),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.action_sync),
                )
            }
        }
    }
}
