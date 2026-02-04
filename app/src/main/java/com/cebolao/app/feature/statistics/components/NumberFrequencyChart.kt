package com.cebolao.app.feature.statistics.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.domain.model.NumberStat

@Composable
fun NumberFrequencyChart(
    stats: List<NumberStat>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
) {
    if (stats.isEmpty()) return

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = density.run { 10.sp.toPx() }
            textAlign = Paint.Align.CENTER
        }
    }

    val maxFreq = remember(stats) { stats.maxOfOrNull { it.frequency }?.toFloat() ?: 1f }
    // Sort by number to display in order (1, 2, 3...) not by freq
    val sortedStats = remember(stats) { stats.sortedBy { it.number } }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val barWidth = size.width / (sortedStats.size * 1.5f)
            val spacing = barWidth * 0.5f
            val maxBarHeight = size.height - 40.dp.toPx() // Leave space for text

            sortedStats.forEachIndexed { index, stat ->
                val freqRatio = stat.frequency / maxFreq
                val barHeight = maxBarHeight * freqRatio
                val x = index * (barWidth + spacing)
                val y = size.height - barHeight - 20.dp.toPx()

                // Draw bar
                drawRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )

                // Draw number label below
                drawContext.canvas.nativeCanvas.drawText(
                    stat.number.toString(),
                    x + barWidth / 2,
                    size.height,
                    textPaint
                )
            }
        }
    }
}
