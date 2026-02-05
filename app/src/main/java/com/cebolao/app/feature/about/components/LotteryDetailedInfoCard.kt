package com.cebolao.app.feature.about.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryInfo
import com.cebolao.domain.model.LotteryProfile

@Composable
fun LotteryDetailedInfoCard(
    profile: LotteryProfile,
    info: LotteryInfo,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(profile.type)

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Header (Always Visible)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onExpandClick() }
                        .padding(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(lotteryColor),
                    )
                    Spacer(modifier = Modifier.size(spacing.sm))
                    Text(
                        text = stringResource(LotteryUiMapper.getNameRes(profile.type)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(300)),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
                    Spacer(modifier = Modifier.height(spacing.md))

                    InfoSection(title = "Como Jogar", content = info.howToPlay, color = lotteryColor)
                    InfoSection(title = "Sorteios", content = info.drawFrequency, color = lotteryColor)
                    InfoSection(title = "Apostas", content = info.betsInfo, color = lotteryColor)
                    InfoSection(title = "Bolão", content = info.bolaoInfo, color = lotteryColor)
                    InfoSection(title = "Premiação", content = info.prizeAllocation, color = lotteryColor)

                    if (info.probabilityInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = "Probabilidades",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = lotteryColor,
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            info.probabilityInfo.forEach { prob ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "${prob.numbersPlayed} números",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = prob.probability,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(top = 4.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.md))
                }
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String,
    color: Color,
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(bottom = spacing.md)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = AlphaLevels.TEXT_MEDIUM),
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2,
        )
    }
}
