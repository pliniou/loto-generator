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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val today = remember { java.time.LocalDate.now() }
    
    // Day of week integer compatible with our logic (1=Sun, ... 7=Sat) if needed, 
    // or just rely on what LotteryScheduleUtil returns.
    // java.time.DayOfWeek: MONDAY=1 ... SUNDAY=7
    // Calendar: SUNDAY=1 ... SATURDAY=7
    // Let's assume LotteryScheduleUtil uses Calendar logic or we match by simple name/enum if possible.
    // For now, let's just get the schedule.
    val schedule = remember { LotteryScheduleUtil.getWeeklySchedule() }
    
    // Find today's schedule
    val dateString = remember {
        com.cebolao.app.util.FormatUtils.formatFriendlyDate(today)
    }

    // Gradient background
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary, // A bit of variation
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Flat on background usually better or low elevation
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(spacing.lg)
        ) {
            Column {
                // Top Row: Greeting & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_greeting), // "Olá, Apostador!"
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = AlphaLevels.TEXT_MEDIUM)
                        )
                    }
                    
                    // Optional: Maybe an icon or logo here
                }
                
                Spacer(modifier = Modifier.height(spacing.xl))
                
                // "Hoje" Section - Highlight what is happening today
                val todaysSchedule = schedule.find { it.dayOfWeek == today.dayOfWeek }
                
                if (todaysSchedule != null && todaysSchedule.lotteries.isNotEmpty()) {
                    Text(
                        text = "Sorteios de Hoje",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = AlphaLevels.TEXT_HIGH),
                        modifier = Modifier.padding(bottom = spacing.sm)
                    )
                    
                    // Horizontal list of today's lotteries pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        items(todaysSchedule.lotteries) { type ->
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape,
                                onClick = { /* Maybe scroll to that lottery? */ }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(LotteryColors.getColor(type), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(
                                        text = stringResource(LotteryUiMapper.getNameRes(type)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                } else {
                     Text(
                        text = "Sem sorteios hoje", // Ou descanso
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = AlphaLevels.TEXT_MEDIUM)
                    )
                }
                
                Spacer(modifier = Modifier.height(spacing.lg))
                
                // Calendar Strip (Simplified)
                Text(
                     text = "Próximos dias",
                     style = MaterialTheme.typography.labelMedium,
                     fontWeight = FontWeight.Bold,
                     color = Color.White.copy(alpha = AlphaLevels.TEXT_HIGH),
                     modifier = Modifier.padding(bottom = spacing.sm)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = schedule, key = { it.dayOfWeek }) { day ->
                        val isToday = day.dayOfWeek == today.dayOfWeek
                        DayCompactCard(day = day, isToday = isToday)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCompactCard(
    day: DaySchedule,
    isToday: Boolean
) {
    val backgroundColor = if (isToday) Color.White else Color.White.copy(alpha = 0.1f)
    val textColor = if (isToday) MaterialTheme.colorScheme.primary else Color.White
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = day.name.take(3), // Seg, Ter...
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        if (day.lotteries.isNotEmpty()) {
             Spacer(modifier = Modifier.height(4.dp))
             Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                 day.lotteries.take(3).forEach { type ->
                     Box(
                         modifier = Modifier
                             .size(4.dp)
                             .background(LotteryColors.getColor(type), CircleShape)
                     )
                 }
                 if (day.lotteries.size > 3) {
                     Box(
                         modifier = Modifier
                             .size(4.dp)
                             .background(textColor.copy(alpha = 0.5f), CircleShape)
                     )
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
