package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RotaryKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    label: String = "BASS"
) {
    var angle by remember { mutableStateOf(valueToAngle(value, valueRange)) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Calculate rotation based on drag
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val touchPoint = change.position
                        val dx = touchPoint.x - center.x
                        val dy = touchPoint.y - center.y
                        var newAngle = atan2(dy.toDouble(), dx.toDouble()).toFloat() * (180f / Math.PI.toFloat())
                        
                        // Adjust angle to start from bottom left and go to bottom right
                        newAngle = (newAngle + 360f) % 360f
                        
                        // Constrain angle to realistic knob limits (e.g., 140 to 40 degrees)
                        if (newAngle in 140f..360f || newAngle in 0f..40f) {
                            angle = newAngle
                            val newValue = angleToValue(angle, valueRange)
                            onValueChange(newValue)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                val radius = size.width / 2
                val center = Offset(size.width / 2, size.height / 2)
                
                // Draw track arc
                drawArc(
                    color = trackColor,
                    startAngle = 140f,
                    sweepAngle = 260f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Calculate sweep based on current value/angle
                val currentSweep = calculateSweepAngle(angle)
                
                // Draw active arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(com.example.ui.theme.SecondaryPurple, primaryColor),
                        center = center
                    ),
                    startAngle = 140f,
                    sweepAngle = currentSweep,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Draw Inner Knob
                drawCircle(
                    color = com.example.ui.theme.SurfaceVariantDark,
                    radius = radius * 0.7f,
                    center = center
                )
                
                // Draw Indicator Dot
                val dotRadius = radius * 0.55f
                val dotAngleRad = (angle) * (Math.PI / 180f)
                val dotX = center.x + dotRadius * cos(dotAngleRad).toFloat()
                val dotY = center.y + dotRadius * sin(dotAngleRad).toFloat()
                
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
            
            // Value Text in Center
            Text(
                text = "${value.roundToInt()}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

private fun valueToAngle(value: Float, range: ClosedFloatingPointRange<Float>): Float {
    val percent = (value - range.start) / (range.endInclusive - range.start)
    val sweep = percent * 260f
    return (140f + sweep) % 360f
}

private fun angleToValue(angle: Float, range: ClosedFloatingPointRange<Float>): Float {
    var sweep = angle - 140f
    if (sweep < 0) sweep += 360f
    val percent = sweep / 260f
    return range.start + (percent * (range.endInclusive - range.start))
}

private fun calculateSweepAngle(angle: Float): Float {
    var sweep = angle - 140f
    if (sweep < 0) sweep += 360f
    return sweep.coerceIn(0f, 260f)
}
