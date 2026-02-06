package com.cebolao.app.feature.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.ConfirmationDialog
import com.cebolao.app.component.DrawingAnimation
import com.cebolao.app.component.GameDetailsDialog
import com.cebolao.app.component.TeamSelectionDialog
import com.cebolao.app.core.UiEvent
import com.cebolao.app.feature.generator.components.GeneratedGameItem
import com.cebolao.app.feature.generator.components.GeneratorConfigSection
import com.cebolao.app.feature.generator.components.GeneratorFilterConfigDialog
import com.cebolao.app.feature.generator.components.GeneratorReportDetailsDialog
import com.cebolao.app.feature.generator.components.GeneratorResultsSection
import com.cebolao.app.feature.generator.components.TimemaniaTeamCard
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.domain.util.LotteryInfoProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GeneratorScreen(
    isLargeScreen: Boolean = false,
    viewModel: GeneratorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val spacing = LocalSpacing.current
    val haptic = LocalHapticFeedback.current
    val savedGamesMessage =
        if (uiState.lastSavedCount > 0) {
            pluralStringResource(
                R.plurals.snackbar_games_saved,
                uiState.lastSavedCount,
                uiState.lastSavedCount,
            )
        } else {
            ""
        }

    var showTeamDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var showSavedConfirmation by remember { mutableStateOf(false) }
    var selectedGameForDetails by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    // Derived states to avoid recomposition when list changes but derived state doesn't
    val hasGeneratedGames by remember { derivedStateOf { uiState.generatedGames.isNotEmpty() } }
    val showActionBar by remember { derivedStateOf { uiState.generatedGames.isNotEmpty() && !uiState.isLoading } }

    // Dialogs
    if (showClearConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_clear_title),
            text = stringResource(R.string.dialog_clear_message),
            confirmText = stringResource(R.string.action_discard),
            onConfirm = {
                viewModel.onClearGenerated()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false },
        )
    }

    if (showTeamDialog) {
        TeamSelectionDialog(
            onDismissRequest = { showTeamDialog = false },
            onTeamSelected = { viewModel.onTeamSelected(it) },
        )
    }

    if (uiState.showFilterConfigDialog) {
        GeneratorFilterConfigDialog(
            uiState = uiState,
            onClose = { viewModel.onCloseFilterConfig() },
            onApplyPreset = { viewModel.onApplyPresetForProfile() },
            onUpdateFilterConfig = { f, cfg -> viewModel.onUpdateFilterConfig(f, cfg) },
            onToggleFilter = { viewModel.onFilterToggled(it) },
        )
    }

    if (uiState.showReportDialog && uiState.generationReport != null) {
        GeneratorReportDetailsDialog(
            uiState = uiState,
            onClose = { viewModel.onCloseReportDetails() },
        )
    }

    // Game details dialog
    selectedGameForDetails?.let { game ->
        GameDetailsDialog(
            game = game,
            lastContest = uiState.lastContest,
            onClose = { selectedGameForDetails = null },
        )
    }

    // Info Bottom Sheet
    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            Box(modifier = Modifier.padding(bottom = spacing.xl)) {
                val info = remember(uiState.selectedType) { LotteryInfoProvider.getInfo(uiState.selectedType) }
                // Use profile if available, otherwise construct a dummy or fetch it.
                // Since LotteryDetailedInfoCard needs a profile, and we have uiState.profile in Generator...
                // Ideally uiState.profile should be non-null if we are in generator.

                if (uiState.profile != null) {
                    com.cebolao.app.feature.about.components.LotteryDetailedInfoCard(
                        profile = uiState.profile!!,
                        info = info,
                        isExpanded = true, // Always expanded in the sheet
                        onExpandClick = {}, // No op or toggle
                        modifier = Modifier.padding(horizontal = spacing.md),
                    )
                }
            }
        }
    }
    // Recommendation Logic
    val recommendedPreset =
        remember(uiState.recommendation, uiState.userPresets) {
            val rec = uiState.recommendation
            if (rec != null) {
                uiState.userPresets.find { it.name == rec.presetName }
            } else {
                null
            }
        }

    // Feedback de salvamento e háptica
    LaunchedEffect(uiState.lastSavedCount) {
        if (uiState.lastSavedCount > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showSavedConfirmation = true
            snackbarHostState.showSnackbar(savedGamesMessage)
            delay(1200)
            showSavedConfirmation = false
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
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
            if (isLargeScreen) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentPadding =
                            PaddingValues(
                                top = spacing.lg,
                                bottom = ComponentDimensions.bottomContentPadding,
                            ),
                        verticalArrangement = Arrangement.spacedBy(spacing.lg),
                    ) {
                        if (recommendedPreset != null && uiState.recommendation != null) {
                            item {
                                RecommendationCard(
                                    stats = uiState.recommendation!!,
                                    onApply = { viewModel.onApplyUserPreset(recommendedPreset) },
                                    onDismiss = { /* Optional: add dismiss logic to VM */ },
                                )
                            }
                        }

                        item {
                            GeneratorConfigSection(
                                selectedType = uiState.selectedType,
                                quantity = uiState.quantity,
                                activeFilters = uiState.activeFilters,
                                filterConfigs = uiState.filterConfigs,
                                profile = uiState.profile,
                                onTypeSelected = { viewModel.onTypeSelected(it) },
                                onQuantityChanged = { viewModel.onQuantityChanged(it) },
                                onFilterToggled = { viewModel.onFilterToggled(it) },
                                onOpenFilterConfig = { viewModel.onOpenFilterConfig() },
                                onInfoClick = { showInfoSheet = true },
                            )
                        }

                        item {
                            TimemaniaTeamCard(
                                selectedType = uiState.selectedType,
                                selectedTeam = uiState.selectedTeam,
                                onShowTeamDialog = { showTeamDialog = true },
                            )
                        }

                        if (!hasGeneratedGames) {
                            item {
                                Spacer(modifier = Modifier.height(spacing.md))
                                Button(
                                    onClick = { viewModel.onGenerate() },
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(ComponentDimensions.generatorButtonHeight),
                                    shape = MaterialTheme.shapes.large,
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = LotteryColors.getColor(uiState.selectedType),
                                            contentColor = LotteryColors.getOnColor(uiState.selectedType),
                                        ),
                                ) {
                                    Text(
                                        text =
                                            pluralStringResource(
                                                R.plurals.action_generate,
                                                uiState.quantity,
                                                uiState.quantity,
                                            ),
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.35f).fillMaxHeight(),
                        contentPadding =
                            PaddingValues(
                                top = spacing.lg,
                                bottom = ComponentDimensions.bottomContentPadding,
                            ),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        if (hasGeneratedGames) {
                            item {
                                GeneratorResultsSection(
                                    generatedCount = uiState.generatedGames.size,
                                    quantity = uiState.quantity,
                                    report = uiState.generationReport,
                                    onOpenReportDetails = { viewModel.onOpenReportDetails() },
                                    onRetry = { viewModel.onGenerate() },
                                )
                            }

                            if (uiState.generatedGames.size > uiState.generatedGamesPageSize) {
                                item {
                                    com.cebolao.app.feature.generator.components.GeneratorPaginationControls(
                                        total = uiState.generatedGames.size,
                                        page = uiState.generatedGamesPage,
                                        pageSize = uiState.generatedGamesPageSize,
                                        onPrevious = { viewModel.onPreviousGeneratedGamesPage() },
                                        onNext = { viewModel.onNextGeneratedGamesPage() },
                                    )
                                }
                            }

                            items(items = uiState.visibleGeneratedGames, key = { it.id }) { game ->
                                GeneratedGameItem(
                                    game = game,
                                    lastContest = uiState.lastContest,
                                    onClick = { selectedGameForDetails = game },
                                    modifier = Modifier,
                                )
                            }
                        } else {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                                    shape = MaterialTheme.shapes.large,
                                ) {
                                    Text(
                                        text = stringResource(R.string.generator_results_placeholder),
                                        modifier = Modifier.padding(spacing.lg),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            top = spacing.lg,
                            bottom = ComponentDimensions.bottomContentPadding,
                        ),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    if (recommendedPreset != null && uiState.recommendation != null) {
                        item {
                            RecommendationCard(
                                stats = uiState.recommendation!!,
                                onApply = { viewModel.onApplyUserPreset(recommendedPreset) },
                                onDismiss = { /* Optional: add dismiss logic to VM */ },
                            )
                        }
                    }

                    item {
                        GeneratorConfigSection(
                            selectedType = uiState.selectedType,
                            quantity = uiState.quantity,
                            activeFilters = uiState.activeFilters,
                            filterConfigs = uiState.filterConfigs,
                            profile = uiState.profile,
                            onTypeSelected = { viewModel.onTypeSelected(it) },
                            onQuantityChanged = { viewModel.onQuantityChanged(it) },
                            onFilterToggled = { viewModel.onFilterToggled(it) },
                            onOpenFilterConfig = { viewModel.onOpenFilterConfig() },
                            onInfoClick = { showInfoSheet = true },
                        )
                    }

                    item {
                        TimemaniaTeamCard(
                            selectedType = uiState.selectedType,
                            selectedTeam = uiState.selectedTeam,
                            onShowTeamDialog = { showTeamDialog = true },
                        )
                    }

                    if (!hasGeneratedGames) {
                        item {
                            Spacer(modifier = Modifier.height(spacing.md))
                            Button(
                                onClick = { viewModel.onGenerate() },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(ComponentDimensions.generatorButtonHeight),
                                shape = MaterialTheme.shapes.large,
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = LotteryColors.getColor(uiState.selectedType),
                                        contentColor = LotteryColors.getOnColor(uiState.selectedType),
                                    ),
                            ) {
                                Text(
                                    text =
                                        pluralStringResource(
                                            R.plurals.action_generate,
                                            uiState.quantity,
                                            uiState.quantity,
                                        ),
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }

                    if (hasGeneratedGames) {
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = spacing.sm),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
                            )
                            GeneratorResultsSection(
                                generatedCount = uiState.generatedGames.size,
                                quantity = uiState.quantity,
                                report = uiState.generationReport,
                                onOpenReportDetails = { viewModel.onOpenReportDetails() },
                                onRetry = { viewModel.onGenerate() },
                            )
                        }

                        if (uiState.generatedGames.size > uiState.generatedGamesPageSize) {
                            item {
                                com.cebolao.app.feature.generator.components.GeneratorPaginationControls(
                                    total = uiState.generatedGames.size,
                                    page = uiState.generatedGamesPage,
                                    pageSize = uiState.generatedGamesPageSize,
                                    onPrevious = { viewModel.onPreviousGeneratedGamesPage() },
                                    onNext = { viewModel.onNextGeneratedGamesPage() },
                                )
                            }
                        }

                        items(items = uiState.visibleGeneratedGames, key = { it.id }) { game ->
                            GeneratedGameItem(
                                game = game,
                                lastContest = uiState.lastContest,
                                onClick = { selectedGameForDetails = game },
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }

            // Overlay de Carregamento (Drawing Animation)
            GeneratorLoading(
                isLoading = uiState.isLoading,
                color = LotteryColors.getColor(uiState.selectedType),
            )

            // Barra de Ações Inferior Redesenhada (Horizontal e Compacta)
            GeneratorBottomBar(
                visible = showActionBar,
                onClear = { showClearConfirmation = true },
                onSave = { viewModel.onSaveAll() },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter),
            )

            GeneratorSaveFeedback(
                visible = showSavedConfirmation,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = spacing.xxxl + spacing.md),
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom =
                                if (uiState.generatedGames.isNotEmpty()) {
                                    ComponentDimensions.bottomBarHeight + spacing.xl
                                } else {
                                    spacing.lg
                                },
                        ),
            )
        }
    }
}

@Composable
private fun GeneratorSaveFeedback(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.generator_saved_confirmation),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun GeneratorLoading(
    isLoading: Boolean,
    color: Color,
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = AlphaLevels.OVERLAY_DARK)),
            contentAlignment = Alignment.Center,
        ) {
            DrawingAnimation(color = color)
        }
    }
}

@Composable
private fun GeneratorBottomBar(
    visible: Boolean,
    onClear: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = AlphaLevels.GLASS_HIGH),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f).height(ComponentDimensions.bottomBarHeight),
                    shape = MaterialTheme.shapes.medium,
                    border =
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_MEDIUM),
                        ),
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(stringResource(R.string.action_discard), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1.5f).height(ComponentDimensions.bottomBarHeight),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(stringResource(R.string.action_save_games), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    stats: com.cebolao.domain.model.UserUsageStats,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star, // Or other icon like Verified/ThumbUp
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(
                    text = "Sugestão para você",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "O preset '${stats.presetName}' tem bons resultados: ${stats.savedGamesCount} jogos salvos e ${stats.usageCount} usos. Que tal usá-lo?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Usar Preset")
            }
        }
    }
}
