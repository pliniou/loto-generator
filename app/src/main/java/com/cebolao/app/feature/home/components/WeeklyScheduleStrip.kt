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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.domain.util.DaySchedule
import java.time.DayOfWeek

@Composable
fun WeeklyScheduleStrip(
    schedule: List<DaySchedule>,
    today: DayOfWeek,
    modifier: Modifier = Modifier,
    selectedDay: DayOfWeek? = null,
    onDayClick: ((DaySchedule) -> Unit)? = null,
) {
    val spacing = LocalSpacing.current

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        items(items = schedule, key = { it.dayOfWeek }) { day ->
            val isToday = day.dayOfWeek == today
            val isSelected = selectedDay?.let { it == day.dayOfWeek } ?: isToday
            WeeklyDayChip(
                day = day,
                isToday = isToday,
                isSelected = isSelected,
                onDayClick = onDayClick,
            )
        }
    }
}

@Composable
private fun WeeklyDayChip(
    day: DaySchedule,
    isToday: Boolean,
    isSelected: Boolean,
    onDayClick: ((DaySchedule) -> Unit)?,
) {
    val spacing = LocalSpacing.current
    val backgroundColor = when {
        isSelected -> Color.White
        else -> Color.White.copy(alpha = 0.12f)
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.White.copy(alpha = AlphaLevels.TEXT_HIGH)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .background(backgroundColor, RoundedCornerShape(spacing.sm))
                .clickable(
                    enabled = onDayClick != null,
                ) { onDayClick?.invoke(day) }
                .padding(horizontal = spacing.sm, vertical = spacing.xs),
    ) {
        Text(
            text = day.name.take(3),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
        if (day.lotteries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                day.lotteries.take(3).forEach { type ->
                    Spacer(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .background(LotteryColors.getColor(type), shape = RoundedCornerShape(3.dp)),
                    )
                }
                if (day.lotteries.size > 3) {
                    Spacer(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .background(textColor.copy(alpha = 0.5f), shape = RoundedCornerShape(3.dp)),
                    )
                }
            }
        }
    }
}
