package com.cebolao.app.feature.games.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.LotteryFilterBar
import com.cebolao.app.component.SmallLotteryBall
import com.cebolao.app.feature.games.RECENT_HIT_RATE_WINDOW
import com.cebolao.app.feature.games.SavedGameCardUiState
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.CebolaoElevation
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.TimemaniaUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val savedGameDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.Builder().setLanguage("pt").setRegion("BR").build())

@Composable
fun GamesFilterBar(
    selectedFilter: LotteryType?,
    totalCount: Int,
    countsByType: Map<LotteryType, Int>,
    onFilterChanged: (LotteryType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    LotteryFilterBar(
        selectedType = selectedFilter,
        onSelectionChanged = onFilterChanged,
        includeAllOption = true,
        totalCount = totalCount,
        countsByType = countsByType,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = spacing.md),
    )
}

@Composable
fun SavedGamesCollection(
    savedGames: List<SavedGameCardUiState>,
    removingGameIds: Set<String>,
    onDelete: (Game) -> Unit,
    onTogglePin: (Game) -> Unit,
    onClick: (Game) -> Unit,
    onAnalyze: (Game) -> Unit,
    onShowHitRateInfo: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val useTwoColumns = maxWidth >= 720.dp
        val contentPadding = PaddingValues(bottom = spacing.xxl)

        if (useTwoColumns) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                gridItems(savedGames, key = { it.game.id }) { savedGame ->
                    AnimatedVisibility(
                        visible = savedGame.game.id !in removingGameIds,
                        enter = expandVertically(animationSpec = tween(durationMillis = 180)) + fadeIn(),
                        exit =
                            shrinkVertically(animationSpec = tween(durationMillis = 180)) +
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth / 2 },
                                    animationSpec = tween(durationMillis = 220),
                                ) +
                                fadeOut(animationSpec = tween(durationMillis = 180)),
                    ) {
                        SavedGameCard(
                            savedGame = savedGame,
                            onDelete = { onDelete(savedGame.game) },
                            onTogglePin = { onTogglePin(savedGame.game) },
                            onClick = { onClick(savedGame.game) },
                            onAnalyze = { onAnalyze(savedGame.game) },
                            onShowHitRateInfo = onShowHitRateInfo,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                items(savedGames, key = { it.game.id }) { savedGame ->
                    AnimatedVisibility(
                        visible = savedGame.game.id !in removingGameIds,
                        enter = expandVertically(animationSpec = tween(durationMillis = 180)) + fadeIn(),
                        exit =
                            shrinkVertically(animationSpec = tween(durationMillis = 180)) +
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth / 2 },
                                    animationSpec = tween(durationMillis = 220),
                                ) +
                                fadeOut(animationSpec = tween(durationMillis = 180)),
                    ) {
                        SavedGameCard(
                            savedGame = savedGame,
                            onDelete = { onDelete(savedGame.game) },
                            onTogglePin = { onTogglePin(savedGame.game) },
                            onClick = { onClick(savedGame.game) },
                            onAnalyze = { onAnalyze(savedGame.game) },
                            onShowHitRateInfo = onShowHitRateInfo,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavedGameCard(
    savedGame: SavedGameCardUiState,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit = {},
    onClick: () -> Unit,
    onAnalyze: () -> Unit = {},
    onShowHitRateInfo: (Int) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val game = savedGame.game
    val lotteryColor = LotteryColors.getColor(game.lotteryType)
    val colorScheme = MaterialTheme.colorScheme
    val insight = remember(game) { com.cebolao.domain.util.StatisticsUtil.analyzeGame(game.numbers, null, emptyList()) }
    val animatedHitRateProgress by animateFloatAsState(
        targetValue = savedGame.recentHitRateProgress,
        animationSpec = tween(durationMillis = 450),
        label = "hitRateProgress",
    )
    val progressColor by androidx.compose.animation.animateColorAsState(
        targetValue = resolveHitRateColor(savedGame.recentHitRateProgress, lotteryColor, colorScheme),
        animationSpec = tween(durationMillis = 350),
        label = "hitRateColor",
    )
    var pinPulseTick by remember(game.id) { mutableIntStateOf(0) }
    val pinScale = remember(game.id) { Animatable(1f) }

    LaunchedEffect(pinPulseTick) {
        if (pinPulseTick == 0) return@LaunchedEffect
        pinScale.snapTo(1f)
        pinScale.animateTo(
            targetValue = 1.18f,
            animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        )
        pinScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing),
        )
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = CebolaoElevation.level2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(ComponentDimensions.strokeWidthMedium, lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT)),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(spacing.lg)
                    .animateContentSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    IconButton(
                        onClick = {
                            pinPulseTick += 1
                            onTogglePin()
                        },
                        modifier =
                            Modifier
                                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                .size(ComponentDimensions.buttonHeightSmall),
                    ) {
                        Icon(
                            imageVector = if (game.isPinned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription =
                                stringResource(
                                    if (game.isPinned) {
                                        R.string.action_unpin
                                    } else {
                                        R.string.action_pin
                                    },
                                ),
                            tint =
                                if (game.isPinned) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW)
                                },
                            modifier =
                                Modifier
                                    .size(ComponentDimensions.iconSizeSmall)
                                    .graphicsLayer {
                                        scaleX = pinScale.value
                                        scaleY = pinScale.value
                                    },
                        )
                    }

                    Text(
                        text = stringResource(LotteryUiMapper.getNameRes(game.lotteryType)),
                        style = MaterialTheme.typography.titleMedium,
                        color = lotteryColor,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier =
                        Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .size(ComponentDimensions.iconSizeMedium),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_remove),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            if (game.teamNumber != null) {
                Text(
                    text = TimemaniaUtil.getTeamName(game.teamNumber),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 7,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                game.numbers.forEach { number ->
                    SmallLotteryBall(number = number, lotteryType = game.lotteryType)
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))
            HorizontalDivider(
                thickness = ComponentDimensions.dividerThicknessThin,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
            )
            Spacer(modifier = Modifier.height(spacing.md))

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    SavedInsightBadge(
                        label = stringResource(R.string.insight_sum, insight.sum),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                    SavedInsightBadge(
                        label = "${insight.evenCount}P / ${insight.oddCount}I",
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                    SavedInsightBadge(
                        label = "Média: ${insight.average}",
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                    SavedInsightBadge(
                        label = "Seq: ${insight.longestSequence}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                    SavedInsightBadge(
                        label = "Múlt3: ${insight.multiplesOf3}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                    SavedInsightBadge(
                        label = "Primos: ${insight.primeCount}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                }

                if (savedGame.showRecentHitRate) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            Text(
                                text =
                                    stringResource(
                                        R.string.games_hit_rate_label,
                                        RECENT_HIT_RATE_WINDOW,
                                        savedGame.recentHitRatePercent,
                                    ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            IconButton(
                                onClick = { onShowHitRateInfo(savedGame.recentHitRatePercent) },
                                modifier = Modifier.sizeIn(minWidth = 40.dp, minHeight = 40.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = stringResource(R.string.games_hit_rate_info),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { animatedHitRateProgress },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(MaterialTheme.shapes.small),
                            color = progressColor,
                            trackColor = colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_MEDIUM),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = onAnalyze,
                        colors = ButtonDefaults.textButtonColors(contentColor = lotteryColor),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = lotteryColor,
                            modifier = Modifier.size(ComponentDimensions.iconSizeSmall),
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(text = stringResource(R.string.action_analyze))
                    }
                }

                Text(
                    text = savedGameDateFormat.format(Date(game.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SavedInsightBadge(
    label: String,
    color: androidx.compose.ui.graphics.Color,
) {
    val spacing = LocalSpacing.current
    Surface(
        color = color,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun resolveHitRateColor(
    progress: Float,
    lotteryColor: Color,
    colorScheme: ColorScheme,
): Color {
    val normalized = progress.coerceIn(0f, 1f)
    if (normalized <= 0.2f) {
        return colorScheme.error
    }
    if (normalized >= 0.65f) {
        return lotteryColor
    }

    val blendFactor = (normalized - 0.2f) / 0.45f
    return lerp(colorScheme.error, lotteryColor, blendFactor)
}
