package com.cebolao.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.LotteryCard
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.FormatUtils
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
) {
    val spacing = LocalSpacing.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        
        if (actionText != null && onActionClick != null) {
            Row(
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .clickable { onActionClick() }
                    .padding(horizontal = spacing.xs, vertical = spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
                if (actionIcon != null) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingDrawsSection(
    contests: Map<LotteryType, com.cebolao.domain.model.Contest?>,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onExpandedChange: (Boolean) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = spacing.sm, horizontal = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            val visibleContests = if (isExpanded) {
                LotteryType.entries
            } else {
                LotteryType.entries.take(3)
            }
            
            visibleContests.forEachIndexed { index, type ->
                val contest = contests[type]
                val lotteryColor = LotteryColors.getColor(type)

                if (contest != null) {
                    NextContestRow(contest, lotteryColor, type)

                    if (index < visibleContests.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = spacing.sm),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
                        )
                    }
                }
            }
            
            if (!isExpanded && LotteryType.entries.size > 3) {
                SeeAllButton(
                    onClick = { onExpandedChange(true) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun LatestResultsSection(
    contests: Map<LotteryType, com.cebolao.domain.model.Contest?>,
    onNavigateToChecker: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onExpandedChange: (Boolean) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val enterDuration = 220
    val exitDuration = 140
    
    val visibleContests = if (isExpanded) {
        LotteryType.entries
    } else {
        LotteryType.entries.take(3)
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        visibleContests.forEach { type ->
            val contest = contests[type]
            AnimatedContent(
                targetState = contest,
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(enterDuration)) +
                        fadeIn(animationSpec = tween(enterDuration))) togetherWith
                        fadeOut(animationSpec = tween(exitDuration))
                },
            ) { animatedContest ->
                LotteryCard(
                    contest = animatedContest,
                    lotteryType = type,
                    onClick = onNavigateToChecker,
                )
            }
        }
        
        if (!isExpanded && LotteryType.entries.size > 3) {
            SeeAllButton(
                onClick = { onExpandedChange(true) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SeeAllButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_MEDIUM),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.sm, horizontal = spacing.lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ver Todos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.size(width = spacing.sm, height = 0.dp))
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(16.dp),
            )
        }
    }
}

@Composable
fun RefreshButton(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
) {
    IconButton(
        onClick = onRefresh,
        modifier = modifier.size(ComponentDimensions.iconSizeExtraLarge),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.action_sync),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun NextContestRow(
    contest: com.cebolao.domain.model.Contest,
    lotteryColor: androidx.compose.ui.graphics.Color,
    type: LotteryType,
) {
    val estimatedPrize = contest.nextContestEstimatedPrize ?: 0.0
    val hasPrize = estimatedPrize > 0
    val isHugeJackpot = estimatedPrize >= 100_000_000 // 100M+

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
                text = "Conc. ${contest.id + 1}$dateText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
            )
        }

        if (hasPrize) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                val formattedPrize = FormatUtils.formatCurrency(estimatedPrize)

                val prizeColor = if (isHugeJackpot) {
                    MaterialTheme.colorScheme.error
                } else {
                    lotteryColor
                }
                
                val prizeStyle = if (isHugeJackpot) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleSmall
                }
                
                Text(
                    text = if (isHugeJackpot) "🔥 $formattedPrize" else formattedPrize,
                    style = prizeStyle,
                    fontWeight = FontWeight.ExtraBold,
                    color = prizeColor,
                )
            }
        } else {
            Text(
                text = "Aguardando",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun SectionDivider(
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    
    HorizontalDivider(
        modifier = modifier.padding(vertical = spacing.lg),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
    )
}
