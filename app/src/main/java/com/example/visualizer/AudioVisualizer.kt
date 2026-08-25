package com.example.visualizer

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")
    
    // We animate a single value to trigger continuous recompositions for the visualizer
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val numBars = 32
        val barWidth = size.width / (numBars * 1.5f)
        val spacing = (size.width - (numBars * barWidth)) / (numBars + 1)
        
        for (i in 0 until numBars) {
            val randomFactor = if (isPlaying) {
                // Faux audio reactivity based on index and phase
                val noise = kotlin.math.sin((i * 0.5f) + (animationPhase * Math.PI * 2)).toFloat()
                val heightPercent = (noise + 1f) / 2f * 0.8f + Random.nextFloat() * 0.2f
                heightPercent * 0.8f + 0.1f // Keep some minimum height
            } else {
                0.05f
            }
            
            val barHeight = size.height * randomFactor
            val x = spacing + i * (barWidth + spacing)
            val y = size.height - barHeight
            
            // Apply EQ-like shaping (higher in middle, lower on edges)
            val envelope = kotlin.math.sin((i.toFloat() / numBars) * Math.PI).toFloat()
            val finalHeight = barHeight * envelope
            val finalY = size.height - finalHeight

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor, Color.Transparent),
                    startY = finalY,
                    endY = size.height
                ),
                topLeft = Offset(x, finalY),
                size = Size(barWidth, finalHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
