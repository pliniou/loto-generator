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
import com.cebolao.app.component.ErrorState
import com.cebolao.app.component.LoadingState
import com.cebolao.app.component.LotteryCard
import com.cebolao.app.component.WelcomeBanner
import com.cebolao.app.core.UiEvent
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.model.LotteryType
import kotlinx.coroutines.flow.collectLatest

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

    // Collect one-shot events for Snackbar
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
            }
        }
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

            if (uiState.isLoading) {
                 LoadingState(modifier = Modifier.fillMaxSize())
            } else if (uiState.error != null) {
                 ErrorState(
                     message = uiState.error?.toUserMessage() ?: stringResource(R.string.state_error),
                     onRetry = { viewModel.refreshData() },
                     modifier = Modifier.fillMaxSize()
                 )
            } else {
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
                    Text(
                        text = stringResource(R.string.home_schedule_title), // "Próximos Sorteios"
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.sm)
                    )
                }

                // Seção de Próximos Concursos (Card unificado)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.lg),
                        shape = MaterialTheme.shapes.large,
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = spacing.md, horizontal = spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            LotteryType.entries.forEachIndexed { index, type ->
                                val contest = uiState.contests[type]
                                val lotteryColor = LotteryColors.getColor(type)

                                if (contest != null) {
                                    NextContestRow(contest, lotteryColor, type)
                                    
                                    if (index < LotteryType.entries.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = spacing.xs),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Text(
                        text = "Últimos Resultados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.sm)
                    )
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
            }

            FloatingActionButton(
                onClick = { viewModel.refreshData() },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = spacing.xl, end = spacing.lg),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.action_sync),
                )
            }
        }
    }
}

    @Composable
    fun NextContestRow(
    contest: com.cebolao.domain.model.Contest,
    lotteryColor: androidx.compose.ui.graphics.Color,
    type: LotteryType
) {
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
            val dateText = contest.nextContestDate?.let { " • $it" } ?: ""
            Text(
                text = "Conc. ${contest.id + 1}${dateText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
            )
        }

        if (hasPrize) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                val formattedPrize = when {
                    estimatedPrize >= 1_000_000_000 -> "R$ ${String.format(java.util.Locale.getDefault(), "%.1f", estimatedPrize / 1_000_000_000.0)}B"
                    estimatedPrize >= 1_000_000 -> "R$ ${String.format(java.util.Locale.getDefault(), "%.1f", estimatedPrize / 1_000_000.0)}M"
                    estimatedPrize >= 1_000 -> "R$ ${String.format(java.util.Locale.getDefault(), "%.0f", estimatedPrize / 1_000.0)}K"
                    else -> "R$ ${estimatedPrize.toLong()}"
                }
                Text(
                    text = formattedPrize,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = lotteryColor,
                )
            }
        } else {
             Text(
                text = "Aguardando",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
