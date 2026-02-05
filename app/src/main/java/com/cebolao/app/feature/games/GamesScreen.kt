package com.cebolao.app.feature.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.EmptyState
import com.cebolao.app.component.GameDetailsDialog
import com.cebolao.app.core.UiEvent
import com.cebolao.app.feature.games.components.SavedGameItem
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel(),
    onNavigateToGenerator: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedGameForDetails by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    var gameToDelete by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    // Derived state to avoid recomposition when games list changes but isEmpty state doesn't
    val isGamesEmpty by remember { derivedStateOf { uiState.games.isEmpty() } }

    if (gameToDelete != null) {
        com.cebolao.app.component.ConfirmationDialog(
            title = stringResource(R.string.dialog_delete_title),
            text = stringResource(R.string.dialog_delete_message),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                gameToDelete?.let { viewModel.onDeleteGame(it) }
            },
            onDismiss = { gameToDelete = null },
        )
    }

    // Game details dialog
    selectedGameForDetails?.let { game ->
        GameDetailsDialog(
            game = game,
            lastContest = null, // In Saved Games we don't necessarily have the last contest context
            onClose = { selectedGameForDetails = null },
        )
    }

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

            Column(modifier = Modifier.fillMaxSize()) {
                // Cabeçalho da Lista
                Text(
                    text = stringResource(R.string.games_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )

                GamesFilterSection(
                    selectedFilter = uiState.filterType,
                    onFilterChanged = viewModel::onFilterChanged,
                )

                if (isGamesEmpty) {
                    GamesEmptyState(onNavigateToGenerator = onNavigateToGenerator)
                } else {
                    GamesList(
                        games = uiState.games,
                        onDelete = { gameToDelete = it },
                        onTogglePin = { viewModel.onTogglePin(it) },
                        onClick = { selectedGameForDetails = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun GamesFilterSection(
    selectedFilter: LotteryType?,
    onFilterChanged: (LotteryType?) -> Unit,
) {
    val spacing = LocalSpacing.current
    LazyRow(
        contentPadding = PaddingValues(vertical = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        item {
            val isSelected = selectedFilter == null
            FilterChip(
                selected = isSelected,
                onClick = { onFilterChanged(null) },
                label = { Text(stringResource(R.string.filter_all)) },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        }
        items(items = LotteryType.entries, key = { it.name }) { type ->
            val isSelected = type == selectedFilter
            // Use static color instead of animation to avoid creating multiple animators
            val chipColor = LotteryColors.getColor(type)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onFilterChanged(null)
                    } else {
                        onFilterChanged(type)
                    }
                },
                label = { Text(stringResource(LotteryUiMapper.getNameRes(type))) },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
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
private fun GamesEmptyState(onNavigateToGenerator: () -> Unit) {
    EmptyState(
        message = stringResource(R.string.state_empty_games),
        actionLabel = stringResource(R.string.state_empty_action),
        onAction = onNavigateToGenerator,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun GamesList(
    games: List<com.cebolao.domain.model.Game>,
    onDelete: (com.cebolao.domain.model.Game) -> Unit,
    onTogglePin: (com.cebolao.domain.model.Game) -> Unit,
    onClick: (com.cebolao.domain.model.Game) -> Unit,
) {
    val spacing = LocalSpacing.current
    LazyColumn(
        contentPadding = PaddingValues(bottom = spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        items(games, key = { it.id }) { game ->
            SavedGameItem(
                game = game,
                onDelete = { onDelete(game) },
                onTogglePin = { onTogglePin(game) },
                onClick = { onClick(game) },
            )
        }
    }
}
