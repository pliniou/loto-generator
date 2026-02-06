package com.cebolao.app.component

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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.feature.home.components.WeeklyScheduleStrip
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.util.LotteryScheduleUtil

@Composable
fun WelcomeBanner(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val today = remember { java.time.LocalDate.now() }
    var selectedDay by remember { mutableStateOf(today.dayOfWeek) }

    // Day of week integer compatible with our logic (1=Sun, ... 7=Sat) if needed,
    // or just rely on what LotteryScheduleUtil returns.
    // java.time.DayOfWeek: MONDAY=1 ... SUNDAY=7
    // Calendar: SUNDAY=1 ... SATURDAY=7
    // Let's assume LotteryScheduleUtil uses Calendar logic or we match by simple name/enum if possible.
    // For now, let's just get the schedule.
    val schedule = remember { LotteryScheduleUtil.getWeeklySchedule() }

    // Find today's schedule
    val dateString =
        remember {
            com.cebolao.app.util.FormatUtils.formatFriendlyDate(today)
        }

    // Gradient background
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val brush =
        remember(primary, tertiary) {
            Brush.linearGradient(
                colors = listOf(primary, tertiary),
            )
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Flat on background usually better or low elevation
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(brush)
                    .padding(spacing.lg),
        ) {
            Column {
                // Top Row: Greeting & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_greeting), // "Olá, Apostador!"
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = AlphaLevels.TEXT_MEDIUM),
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
                        modifier = Modifier.padding(bottom = spacing.sm),
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Horizontal list of today's lotteries pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        items(todaysSchedule.lotteries) { type ->
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape,
                                onClick = { /* Maybe scroll to that lottery? */ },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .sizeIn(minHeight = 48.dp)
                                        .padding(horizontal = spacing.sm, vertical = spacing.xs),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(8.dp)
                                                .background(LotteryColors.getColor(type), CircleShape),
                                    )
                                    Spacer(modifier = Modifier.width(spacing.sm))
                                    Text(
                                        text = stringResource(LotteryUiMapper.getNameRes(type)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Sem sorteios hoje", // Ou descanso
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                    )
                }

                Spacer(modifier = Modifier.height(spacing.lg))

                // Calendar Strip (Simplified)
                Text(
                    text = "Próximos dias",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = AlphaLevels.TEXT_HIGH),
                    modifier = Modifier.padding(bottom = spacing.xs),
                )

                WeeklyScheduleStrip(
                    schedule = schedule,
                    today = today.dayOfWeek,
                    selectedDay = selectedDay,
                    onDayClick = { day ->
                        selectedDay = day.dayOfWeek
                    },
                )
            }
        }
    }
}
