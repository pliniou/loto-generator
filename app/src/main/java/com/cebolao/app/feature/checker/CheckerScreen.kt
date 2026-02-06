package com.cebolao.app.feature.checker

import android.animation.ValueAnimator
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.ScannerAnimation
import com.cebolao.app.feature.checker.components.AnalysisStatsSection
import com.cebolao.app.feature.checker.components.CheckerContestSelector
import com.cebolao.app.feature.checker.components.CheckerDuplaModeSelector
import com.cebolao.app.feature.checker.components.CheckerNumberGrid
import com.cebolao.app.feature.checker.components.CheckerResultCard
import com.cebolao.app.feature.checker.components.CheckerSectionSubheading
import com.cebolao.app.feature.checker.components.CheckerTypeSelector
import com.cebolao.app.feature.checker.components.HistoryHitsList
import com.cebolao.app.feature.checker.components.HistorySummaryRow
import com.cebolao.app.feature.checker.components.NumberStatsSection
import com.cebolao.app.feature.checker.StatsFilter
import com.cebolao.app.feature.checker.components.StatsFilterSelector
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.TimemaniaUtil
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.Contest

data class CheckerPrefillArgs(
    val type: LotteryType,
    val numbers: List<Int>,
    val teamNumber: Int? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckerScreen(
    viewModel: CheckerViewModel = hiltViewModel(),
    prefillArgs: CheckerPrefillArgs? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val haptic = LocalHapticFeedback.current
    val lotteryColor = LotteryColors.getColor(uiState.selectedType)

    var showTeamDialog by remember { mutableStateOf(false) }

    LaunchedEffect(prefillArgs) {
        prefillArgs?.let { args ->
            viewModel.applyPrefillOnce(args.type, args.numbers, args.teamNumber)
        }
    }

    if (showTeamDialog) {
        com.cebolao.app.component.TeamSelectionDialog(
            onDismissRequest = { showTeamDialog = false },
            onTeamSelected = { viewModel.onTeamSelected(it) },
        )
    }

    // Integração de háptica
    LaunchedEffect(uiState.checkResult) {
        if (uiState.checkResult != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.isAnalyzing) {
        if (uiState.isAnalyzing) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Validação de seleção
    val isSelectionValid =
        if (uiState.selectedType == LotteryType.SUPER_SETE) {
            uiState.selectedNumbers.size == 7 && uiState.selectedNumbers.none { it == -1 }
        } else {
            val required = uiState.profile?.numbersPerGame
            if (required == null) {
                uiState.selectedNumbers.isNotEmpty()
            } else {
                uiState.selectedNumbers.size == required
            }
        }

    val hasResults = uiState.checkResult != null || uiState.analysisResults.isNotEmpty()
    val isAnalyzing = uiState.isAnalyzing
    val screenEdgePadding = 16.dp
    val sectionSpacing = 8.dp
    val reduceMotion =
        remember {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()
        }
    val enterDuration = if (reduceMotion) 0 else 220
    val exitDuration = if (reduceMotion) 0 else 120
    val contentSizeDuration = if (reduceMotion) 0 else 240

    CebolaoContent {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth > 600.dp
            
            if (isWide) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = screenEdgePadding),
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg)
                ) {
                    // Left Pane: Inputs & Result
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .animateContentSize(animationSpec = tween(contentSizeDuration)),
                        contentPadding = PaddingValues(bottom = spacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                    ) {
                        checkerInputSection(
                            uiState = uiState,
                            lotteryColor = lotteryColor,
                            showTeamDialog = { showTeamDialog = true },
                            onTypeSelected = viewModel::onTypeSelected,
                            onDuplaModeSelected = viewModel::onDuplaModeSelected,
                            onSuperSeteSelected = viewModel::onSuperSeteNumberSelected,
                            onNumberToggle = viewModel::onNumberToggle,
                            onContestSelectorToggle = viewModel::onContestSelectorToggle,
                            onContestSearchQueryChanged = viewModel::onContestSearchQueryChanged,
                            onContestSelected = viewModel::onContestSelected,
                            animationDurationMs = enterDuration,
                        )
                        
                        item {
                             CheckActionButton(
                                isAnalyzing = isAnalyzing,
                                enabled = isSelectionValid && uiState.lastContest != null && !isAnalyzing,
                                onClick = {
                                    viewModel.onCheck()
                                    viewModel.onAnalyzeHistory()
                                },
                                color = lotteryColor,
                                onColor = LotteryColors.getOnColor(uiState.selectedType)
                             )
                        }

                        checkerResultSection(
                            uiState = uiState,
                            hasResults = hasResults,
                            enterDurationMs = enterDuration,
                            exitDurationMs = exitDuration,
                        )
                    }

                    // Right Pane: Stats
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .animateContentSize(animationSpec = tween(contentSizeDuration)),
                        contentPadding = PaddingValues(bottom = spacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                    ) {
                        checkerStatsSection(
                            uiState = uiState,
                            hasResults = hasResults,
                            lotteryColor = lotteryColor,
                            onFilterSelected = viewModel::onStatsFilterSelected,
                            animationDurationMs = enterDuration,
                        )
                    }
                }
            } else {
                // Mobile Layout (Single Column)
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = screenEdgePadding)
                            .animateContentSize(animationSpec = tween(contentSizeDuration)),
                    contentPadding = PaddingValues(bottom = spacing.xxl),
                    verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                ) {
                    checkerInputSection(
                        uiState = uiState,
                        lotteryColor = lotteryColor,
                        showTeamDialog = { showTeamDialog = true },
                        onTypeSelected = viewModel::onTypeSelected,
                        onDuplaModeSelected = viewModel::onDuplaModeSelected,
                        onSuperSeteSelected = viewModel::onSuperSeteNumberSelected,
                        onNumberToggle = viewModel::onNumberToggle,
                        onContestSelectorToggle = viewModel::onContestSelectorToggle,
                        onContestSearchQueryChanged = viewModel::onContestSearchQueryChanged, 
                        onContestSelected = viewModel::onContestSelected,
                        animationDurationMs = enterDuration,
                    )

                    item {
                         CheckActionButton(
                            isAnalyzing = isAnalyzing,
                            enabled = isSelectionValid && uiState.lastContest != null && !isAnalyzing,
                            onClick = {
                                viewModel.onCheck()
                                viewModel.onAnalyzeHistory()
                            },
                            color = lotteryColor,
                            onColor = LotteryColors.getOnColor(uiState.selectedType)
                         )
                    }

                    checkerResultSection(
                        uiState = uiState,
                        hasResults = hasResults,
                        enterDurationMs = enterDuration,
                        exitDurationMs = exitDuration,
                    )

                    checkerStatsSection(
                        uiState = uiState,
                        hasResults = hasResults,
                        lotteryColor = lotteryColor,
                        onFilterSelected = viewModel::onStatsFilterSelected,
                        animationDurationMs = enterDuration,
                    )
                }
            }

            // Scanner Overlay
            AnimatedVisibility(
                visible = uiState.isAnalyzing,
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
                    ScannerAnimation(color = lotteryColor)
                }
            }
        }
    }
}

// --- LazyListScope Extensions for Sections ---

private fun androidx.compose.foundation.lazy.LazyListScope.checkerInputSection(
    uiState: CheckerUiState,
    lotteryColor: Color,
    showTeamDialog: () -> Unit,
    onTypeSelected: (LotteryType) -> Unit,
    onDuplaModeSelected: (DuplaMode) -> Unit,
    onSuperSeteSelected: (Int, Int) -> Unit,
    onNumberToggle: (Int) -> Unit,
    onContestSelectorToggle: (Boolean) -> Unit,
    onContestSearchQueryChanged: (String) -> Unit,
    onContestSelected: (Contest) -> Unit,
    animationDurationMs: Int,
) {
    // Cabeçalho
    item {
        Text(
            text = stringResource(R.string.checker_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
    }

    // 1. Seletor de tipo (Chips Neon)
    item {
        CheckerTypeSelector(
            selectedType = uiState.selectedType,
            onTypeSelected = onTypeSelected,
        )
    }

    // 1.5 Modo Dupla Sena
    if (uiState.selectedType == LotteryType.DUPLA_SENA) {
        item {
            CheckerDuplaModeSelector(
                selectedMode = uiState.selectedDuplaMode,
                onModeSelected = onDuplaModeSelected,
            )
        }
    }

    // 2. Seletor de Concurso
    item {
        val currentContest = uiState.lastContest
        if (uiState.availableContests.isNotEmpty()) {
             CheckerContestSelector(
                currentContest = currentContest,
                selectedType = uiState.selectedType,
                isExpanded = uiState.isContestSelectorExpanded,
                searchQuery = uiState.contestSearchQuery,
                onExpandedChange = onContestSelectorToggle,
                onQueryChange = onContestSearchQueryChanged,
                onContestSelected = onContestSelected,
                availableContests = uiState.availableContests,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // 3. Grade de números ou Super Sete
    item {
        if (uiState.selectedType == LotteryType.SUPER_SETE) {
            com.cebolao.app.component.SuperSeteInput(
                selectedNumbers = uiState.selectedNumbers,
                onNumberClick = onSuperSeteSelected,
            )
        } else {
            val fontScale = LocalDensity.current.fontScale.coerceIn(1f, 1.25f)
            val gridHeight = (280f * fontScale).dp
            // Grid com altura fixa para garantir scroll
            Crossfade(
                targetState = Triple(uiState.selectedNumbers, uiState.matchedNumbers, uiState.checkResult?.contestNumber),
                animationSpec = tween(animationDurationMs),
                label = "checker-grid-crossfade",
            ) { gridState ->
                key(gridState) {
                    Box(modifier = Modifier.height(gridHeight)) {
                        CheckerNumberGrid(
                            uiState = uiState,
                            onNumberToggle = onNumberToggle,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }

    // 3.5 Time do Coração (Timemania)
    if (uiState.selectedType == LotteryType.TIMEMANIA) {
        item {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .semantics { role = Role.Button }
                        .clickable { showTeamDialog() },
                shape = MaterialTheme.shapes.medium,
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                    ),
                border = androidx.compose.foundation.BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_LOW)),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            stringResource(R.string.timemania_team_heart),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text =
                                uiState.selectedTeam?.let { TimemaniaUtil.getTeamName(it) }
                                    ?: stringResource(R.string.checker_select_team),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Icon(Icons.Default.Add, contentDescription = null, tint = lotteryColor)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.checkerResultSection(
    uiState: CheckerUiState,
    hasResults: Boolean,
    enterDurationMs: Int,
    exitDurationMs: Int,
) {
    if (uiState.checkResult != null) {
        item {
            AnimatedVisibility(
                visible = hasResults,
                enter = fadeIn(animationSpec = tween(durationMillis = enterDurationMs)),
                exit = fadeOut(animationSpec = tween(durationMillis = exitDurationMs)),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckerResultCard(uiState = uiState)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.checkerStatsSection(
    uiState: CheckerUiState,
    hasResults: Boolean,
    lotteryColor: Color,
    onFilterSelected: (StatsFilter) -> Unit,
    animationDurationMs: Int,
) {
    if (hasResults && uiState.totalContestsChecked > 0) {
        item {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Estatísticas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = lotteryColor,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Filtro de Estatísticas
        item {
            AnimatedVisibility(
                visible = hasResults,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(
                                durationMillis = animationDurationMs,
                                delayMillis = if (animationDurationMs == 0) 0 else 40,
                            ),
                    ),
            ) {
                StatsFilterSelector(
                    currentFilter = uiState.statsFilter,
                    onFilterSelected = onFilterSelected,
                    color = lotteryColor
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = hasResults,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(
                                durationMillis = animationDurationMs,
                                delayMillis = if (animationDurationMs == 0) 0 else 80,
                            ),
                    ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckerSectionSubheading(text = "Resumo Histórico")
                    HistorySummaryRow(
                        total = uiState.totalContestsChecked,
                        best = uiState.bestHit,
                        prizeCount = uiState.prizeCount,
                        color = lotteryColor,
                    )
                }
            }
        }
    }

    // Análise de acertos históricos (inline)
    if (uiState.analysisResults.isNotEmpty()) {
        item {
            AnimatedVisibility(
                visible = hasResults,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(
                                durationMillis = animationDurationMs,
                                delayMillis = if (animationDurationMs == 0) 0 else 120,
                            ),
                    ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckerSectionSubheading(text = "Distribuição de Acertos")
                    AnalysisStatsSection(
                        results = uiState.analysisResults,
                        color = lotteryColor,
                        prizeRanges = uiState.profile?.prizeRanges.orEmpty(),
                    )
                }
            }
        }
    }

    // Gráfico de distribuição de acertos
    if (uiState.historyResults.isNotEmpty()) {
        item {
            AnimatedVisibility(
                visible = hasResults,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(
                                durationMillis = animationDurationMs,
                                delayMillis = if (animationDurationMs == 0) 0 else 160,
                            ),
                    ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckerSectionSubheading(text = "Distribuição de Acertos por Concurso")
                    HistoryHitsList(
                        history = uiState.historyResults,
                        color = lotteryColor,
                        prizeThreshold = uiState.profile?.prizeRanges?.minOrNull(),
                    )
                }
            }
        }
    }

    // Estatísticas de números (frequência e atraso)
    if (uiState.numberStats.isNotEmpty()) {
        item {
            AnimatedVisibility(
                visible = hasResults,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(
                                durationMillis = animationDurationMs,
                                delayMillis = if (animationDurationMs == 0) 0 else 200,
                            ),
                    ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckerSectionSubheading(text = "Frequência das Dezenas")
                    NumberStatsSection(
                        stats = uiState.numberStats,
                        color = lotteryColor,
                        selectedNumbers = uiState.selectedNumbers,
                    )
                }
            }
        }
    }
    
    item {
         Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CheckActionButton(
    isAnalyzing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    color: Color,
    onColor: Color
) {
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = onColor,
            ),
    ) {
        Text(
            text = if (isAnalyzing) "Analisando..." else stringResource(R.string.action_check) + " & " + stringResource(R.string.action_analyze),
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
