package com.cebolao.app.feature.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.ConfirmationDialog
import com.cebolao.app.component.EmptyState
import com.cebolao.app.component.GameDetailsDialog
import com.cebolao.app.core.UiEvent
import com.cebolao.app.feature.games.components.GamesFilterBar
import com.cebolao.app.feature.games.components.SavedGamesCollection
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.domain.model.Game
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val REMOVAL_ANIMATION_DELAY_MS = 220L

@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel(),
    onNavigateToGenerator: () -> Unit = {},
    onNavigateToChecker: (Game) -> Unit = {},
) {
    val resources = LocalResources.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedGameForDetails by remember { mutableStateOf<Game?>(null) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    val pendingRemovalIds = remember { mutableStateListOf<String>() }

    val isGamesEmpty by remember { derivedStateOf { uiState.savedGames.isEmpty() } }
    val undoLabel = stringResource(R.string.action_undo)
    val gameRemovedMessage = stringResource(R.string.games_remove_snackbar)

    gameToDelete?.let {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_delete_title),
            text = stringResource(R.string.dialog_delete_message),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                if (!pendingRemovalIds.contains(it.id)) {
                    pendingRemovalIds.add(it.id)
                    coroutineScope.launch {
                        delay(REMOVAL_ANIMATION_DELAY_MS)
                        val snackbarResult =
                            snackbarHostState.showSnackbar(
                                message = gameRemovedMessage,
                                actionLabel = undoLabel,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        if (snackbarResult != SnackbarResult.ActionPerformed) {
                            viewModel.onDeleteGame(it)
                        }
                        pendingRemovalIds.remove(it.id)
                    }
                }
                gameToDelete = null
            },
            onDismiss = { gameToDelete = null },
        )
    }

    selectedGameForDetails?.let { game ->
        GameDetailsDialog(
            game = game,
            lastContest = null,
            onClose = { selectedGameForDetails = null },
        )
    }

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
                snackbar = { snackbarData ->
                    SlideUpSnackbar(snackbarData)
                },
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.games_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )

                GamesFilterBar(
                    selectedFilter = uiState.filterType,
                    totalCount = uiState.totalCount,
                    countsByType = uiState.countsByType,
                    onFilterChanged = viewModel::onFilterChanged,
                )

                if (isGamesEmpty) {
                    EmptyState(
                        message = stringResource(R.string.state_empty_games),
                        actionLabel = stringResource(R.string.state_empty_action),
                        onAction = onNavigateToGenerator,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    SavedGamesCollection(
                        savedGames = uiState.savedGames,
                        removingGameIds = pendingRemovalIds.toSet(),
                        onDelete = { gameToDelete = it },
                        onTogglePin = viewModel::onTogglePin,
                        onClick = { selectedGameForDetails = it },
                        onAnalyze = onNavigateToChecker,
                        onShowHitRateInfo = { percent ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message =
                                        resources.getQuantityString(
                                            R.plurals.games_hit_rate_info_message,
                                            RECENT_HIT_RATE_WINDOW,
                                            RECENT_HIT_RATE_WINDOW,
                                            percent,
                                        ),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SlideUpSnackbar(snackbarData: SnackbarData) {
    val visibilityState = remember { MutableTransitionState(false) }
    LaunchedEffect(snackbarData.visuals.message) {
        visibilityState.targetState = true
    }

    AnimatedVisibility(
        visibleState = visibilityState,
        enter =
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 2 },
                animationSpec = tween(durationMillis = 220),
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
    ) {
        Snackbar(snackbarData = snackbarData)
    }
}
