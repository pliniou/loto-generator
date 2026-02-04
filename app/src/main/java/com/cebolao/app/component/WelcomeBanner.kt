package com.cebolao.app.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.util.DaySchedule
import com.cebolao.domain.util.LotteryScheduleUtil

@Composable
fun WelcomeBanner(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val today = remember { java.time.LocalDate.now() }
    today.dayOfWeek.value + 1 // java.time uses 1 (Mon) to 7 (Sun), Calendar uses 1 (Sun) to 7 (Sat)
    // Adjusting to match Calendar.DAY_OF_WEEK logic if needed or just use java.time logic.
    // However, LotteryScheduleUtil likely uses Calendar constants (1=Sunday).
    // java.time: Mon=1, Sun=7.
    // Calendar: Sun=1, Mon=2.
    // Conversion: (dayOfWeek.value % 7) + 1
    val dayOfWeekCalendarStyle = (today.dayOfWeek.value % 7) + 1

    val dateString =
        remember {
            com.cebolao.app.util.FormatUtils.formatFriendlyDate(today)
        }

    val schedule = remember { LotteryScheduleUtil.getWeeklySchedule() }

    // State to track which day is expanded
    var expandedDay by remember { mutableStateOf<Int?>(null) }

    // Fundo com gradiente (cores reforçadas)
    val brush =
        Brush.verticalGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer,
                ),
        )

    // Animação de elevação suave
    val elevation by animateDpAsState(
        targetValue = 4.dp,
        animationSpec = tween(durationMillis = 300),
        label = "banner-elevation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation,
                pressedElevation = 6.dp,
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(brush),
        ) {
            Column(
                modifier = Modifier.padding(spacing.xl),
            ) {
                // Cabeçalho
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = AlphaLevels.TEXT_HIGH),
                    modifier = Modifier.padding(top = spacing.xs),
                )

                Spacer(modifier = Modifier.height(spacing.xl))

                // Calendar Section Title
                Text(
                    text = stringResource(R.string.home_schedule_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = spacing.md),
                )

                // Enhanced Calendar Week
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(items = schedule, key = { it.dayOfWeekConstant }) { day ->
                        EnhancedDayScheduleCard(
                            day = day,
                            isToday = day.dayOfWeekConstant == dayOfWeekCalendarStyle,
                            isExpanded = expandedDay == day.dayOfWeekConstant,
                            onClick = {
                                expandedDay =
                                    if (expandedDay == day.dayOfWeekConstant) {
                                        null // Collapse if same day clicked
                                    } else {
                                        day.dayOfWeekConstant // Expand this day
                                    }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedDayScheduleCard(
    day: DaySchedule,
    isToday: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current

    // Animated colors and sizes
    val backgroundColor =
        when {
            isExpanded -> Color.White
            isToday -> Color.White.copy(alpha = AlphaLevels.GLASS_LOW)
            else -> Color.White.copy(alpha = AlphaLevels.MINIMAL)
        }

    val textColor =
        when {
            isExpanded -> MaterialTheme.colorScheme.primary
            isToday -> Color.White
            else -> Color.White.copy(alpha = AlphaLevels.TEXT_MEDIUM)
        }

    val elevation by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 300),
        label = "card-elevation",
    )

    Card(
        modifier =
            Modifier
                .width(if (isExpanded) 140.dp else 72.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300),
                )
                .clickable(enabled = day.lotteries.isNotEmpty()) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(
                        horizontal = spacing.md,
                        vertical = spacing.md,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Day Name
            Text(
                text = day.name,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = if (isToday || isExpanded) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // Lottery Indicators or Names
            if (day.lotteries.isNotEmpty()) {
                AnimatedVisibility(
                    visible = !isExpanded,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200)),
                ) {
                    // Compact view: colored dots
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val displayLotteries = day.lotteries.take(4)
                        val rows = displayLotteries.chunked(2)

                        rows.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                row.forEach { type ->
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(if (isToday) 10.dp else 8.dp)
                                                .clip(CircleShape)
                                                .background(LotteryColors.getColor(type)),
                                    )
                                }
                            }
                        }

                        // Show count if more than 4
                        if (day.lotteries.size > 4) {
                            Text(
                                text = "+${day.lotteries.size - 4}",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter =
                        expandVertically(
                            animationSpec = tween(300),
                            expandFrom = Alignment.Top,
                        ) + fadeIn(animationSpec = tween(300)),
                    exit =
                        shrinkVertically(
                            animationSpec = tween(300),
                            shrinkTowards = Alignment.Top,
                        ) + fadeOut(animationSpec = tween(300)),
                ) {
                    // Expanded view: lottery names
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        day.lotteries.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(LotteryColors.getColor(type)),
                                )

                                Spacer(modifier = Modifier.width(spacing.sm))

                                Text(
                                    text = stringResource(LotteryUiMapper.getNameRes(type)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty day
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
