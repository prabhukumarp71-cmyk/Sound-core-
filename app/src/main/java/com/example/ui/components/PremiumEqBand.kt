package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = -12f..12f,
    modifier: Modifier = Modifier
) {
    var height by remember { mutableStateOf(0f) }
    
    Box(
        modifier = modifier
            .width(40.dp)
            .height(150.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val rangeSize = valueRange.endInclusive - valueRange.start
                    val dragPercent = -dragAmount / size.height
                    val newValue = (value + dragPercent * rangeSize).coerceIn(valueRange)
                    onValueChange(newValue)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Track
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        
        // Active Track & Thumb
        Canvas(modifier = Modifier.fillMaxHeight().width(40.dp)) {
            val rangeSize = valueRange.endInclusive - valueRange.start
            val percent = (value - valueRange.start) / rangeSize
            val yOffset = size.height - (size.height * percent)
            
            // Draw active track
            drawLine(
                color = com.example.ui.theme.PrimaryNeonBlue,
                start = Offset(size.width / 2, size.height),
                end = Offset(size.width / 2, yOffset),
                strokeWidth = 4.dp.toPx()
            )
            
            // Draw thumb
            drawCircle(
                color = com.example.ui.theme.TextPrimary,
                radius = 8.dp.toPx(),
                center = Offset(size.width / 2, yOffset)
            )
            drawCircle(
                color = com.example.ui.theme.PrimaryNeonBlue,
                radius = 4.dp.toPx(),
                center = Offset(size.width / 2, yOffset)
            )
        }
    }
}

@Composable
fun PremiumEqBand(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${if (value > 0) "+" else ""}${value.roundToInt()}dB",
            style = MaterialTheme.typography.labelSmall,
            color = if (value != 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        VerticalSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}
