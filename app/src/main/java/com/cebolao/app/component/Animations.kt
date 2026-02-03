package com.cebolao.app.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.app.theme.AlphaLevels

@Composable
fun DrawingAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "drawing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "scale",
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = AlphaLevels.MEDIUM_LOW,
        targetValue = AlphaLevels.FULL,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "opacity",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Pulse Ring
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .scale(scale)
                        .background(color.copy(alpha = AlphaLevels.MINIMAL), CircleShape),
            )
            // Core Ball
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = color,
                shadowElevation = 8.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
        Text(
            text = "Sorteando...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = opacity),
        )
    }
}

@Composable
fun ScannerAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "offset",
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            // Scanner Frame
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .border(2.dp, color.copy(alpha = AlphaLevels.BORDER_LOW), MaterialTheme.shapes.medium),
            )

            // Scanning Line
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.9f)
                        .height(2.dp)
                        .offset(y = offsetY.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, color, Color.Transparent),
                            ),
                        ),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Analisando histórico...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}
