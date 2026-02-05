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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.domain.model.NumberStat

@Composable
fun NumberRecencyChart(
    stats: List<NumberStat>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.tertiary,
    dotColor: Color = MaterialTheme.colorScheme.error,
) {
    if (stats.isEmpty()) return

    val density = LocalDensity.current
    val textPaint =
        remember(density) {
            Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = density.run { 10.sp.toPx() }
                textAlign = Paint.Align.CENTER
            }
        }

    val maxDelay = remember(stats) { stats.maxOfOrNull { it.delay }?.toFloat() ?: 10f }
    val sortedStats = remember(stats) { stats.sortedBy { it.number } }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val stepX = size.width / (sortedStats.size - 1).coerceAtLeast(1)
            val maxY = size.height - 40.dp.toPx()

            val path = Path()

            sortedStats.forEachIndexed { index, stat ->
                val delayRatio = stat.delay / maxDelay
                // Higher delay = higher point visually? Or lower?
                // Let's make higher delay = higher bar/point
                val y = maxY - (maxY * delayRatio)
                val x = index * stepX

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // Draw dot
                if (stat.delay > maxDelay * 0.7) { // Highlight very late numbers
                    drawCircle(
                        color = dotColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y),
                    )
                } else {
                    drawCircle(
                        color = lineColor,
                        radius = 2.dp.toPx(),
                        center = Offset(x, y),
                    )
                }

                // Draw number label every 5 numbers to avoid clutter
                if (index % 5 == 0 || index == sortedStats.lastIndex) {
                    drawContext.canvas.nativeCanvas.drawText(
                        stat.number.toString(),
                        x,
                        size.height,
                        textPaint,
                    )
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}
