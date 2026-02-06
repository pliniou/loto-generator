package com.cebolao.app.feature.about.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cebolao.R
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
    val headerBackground = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
    val titleColor = if (isExpanded) lotteryColor else MaterialTheme.colorScheme.onSurface
    val titleStyle = if (isExpanded) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium
    val arrowTint = if (isExpanded) lotteryColor else MaterialTheme.colorScheme.onSurfaceVariant
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "aboutArrowRotation",
    )
    val uriHandler = LocalUriHandler.current
    val sourceUrl = stringResource(R.string.about_source_url)
    val sourceLabel = stringResource(R.string.about_source_label)

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
                        .background(headerBackground)
                        .clickable { onExpandClick() }
                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                        style = titleStyle,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        modifier = Modifier.weight(1f),
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = arrowTint,
                    modifier =
                        Modifier
                            .padding(start = spacing.sm, top = 2.dp)
                            .rotate(arrowRotation),
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(240)) + fadeIn(tween(220)) + slideInVertically(initialOffsetY = { -it / 8 }, animationSpec = tween(220)),
                exit = shrinkVertically(tween(220)) + fadeOut(tween(180)) + slideOutVertically(targetOffsetY = { -it / 8 }, animationSpec = tween(180)),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
                    Spacer(modifier = Modifier.height(spacing.md))

                    Column(modifier = Modifier.padding(start = spacing.md, end = spacing.xs)) {
                        InfoSection(
                            title = "Como Jogar",
                            content = info.howToPlay,
                            color = lotteryColor,
                            icon = Icons.Default.Casino,
                        )
                        InfoSection(
                            title = "Sorteios",
                            content = info.drawFrequency,
                            color = lotteryColor,
                            icon = Icons.Default.Event,
                        )
                        InfoSection(
                            title = "Apostas",
                            content = info.betsInfo,
                            color = lotteryColor,
                            icon = Icons.Default.ConfirmationNumber,
                        )
                        InfoSection(
                            title = "Bolão",
                            content = info.bolaoInfo,
                            color = lotteryColor,
                            icon = Icons.Default.Groups,
                        )
                        InfoSection(
                            title = "Premiação",
                            content = info.prizeAllocation,
                            color = lotteryColor,
                            icon = Icons.Default.EmojiEvents,
                        )

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
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
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

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { uriHandler.openUri(sourceUrl) }
                                    .padding(vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = sourceLabel,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                            )
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
    icon: ImageVector,
) {
    val spacing = LocalSpacing.current
    val bullets = content.toSectionBullets()
    val sectionTitleStyle = MaterialTheme.typography.labelLarge.copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
    val contentTextStyle = MaterialTheme.typography.bodySmall

    Column(modifier = Modifier.padding(bottom = spacing.md)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = title,
                style = sectionTitleStyle,
                color = color,
            )
        }
        Spacer(modifier = Modifier.height(spacing.xs))
        if (bullets.isEmpty()) {
            Text(
                text = content,
                style = contentTextStyle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                lineHeight = contentTextStyle.lineHeight * 1.2,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                bullets.forEach { bullet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(color),
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Text(
                            text = bullet,
                            style = contentTextStyle,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                            lineHeight = contentTextStyle.lineHeight * 1.2,
                        )
                    }
                }
            }
        }
    }
}

private fun String.toSectionBullets(): List<String> =
    replace("\n", " ")
        .split(". ")
        .mapNotNull { sentence ->
            val trimmed = sentence.trim().trimEnd('.')
            if (trimmed.isBlank()) null else "$trimmed."
        }
