package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.SageDark
import com.example.ui.theme.SagePrimary
import kotlinx.coroutines.delay

@Composable
fun HangingNestyRobot(
    modifier: Modifier = Modifier,
    isThinking: Boolean = false,
    onClick: () -> Unit = {}
) {
    var showTipBubble by remember { mutableStateOf(false) }

    // Auto-hide speech bubble
    LaunchedEffect(showTipBubble) {
        if (showTipBubble) {
            delay(3500)
            showTipBubble = false
        }
    }

    // Gentle floating idle animation
    val infiniteTransition = rememberInfiniteTransition(label = "nesty_idle")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Subtle gentle breath/sway
    val antennaGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "antennaGlow"
    )

    // Blinking eye animation
    var eyeBlinkScale by remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((3000..6000).random().toLong())
            eyeBlinkScale = 0.1f // Blink closed
            delay(120)
            eyeBlinkScale = 1f // Eyes open
        }
    }

    Box(
        modifier = modifier
            .offset(y = floatOffset.dp)
            .testTag("hanging_nesty_robot"),
        contentAlignment = Alignment.TopEnd
    ) {
        // Speech Bubble if clicked or thinking
        AnimatedVisibility(
            visible = showTipBubble || isThinking,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .offset(x = (-68).dp, y = (-26).dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LavenderPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isThinking) "Nesty is solving..." else "Hi! Nesty is ready ✨",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SageDark
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // The Cute Robotic Nesty Mascot
        Box(
            modifier = Modifier
                .size(width = 68.dp, height = 58.dp)
                .clickable {
                    showTipBubble = !showTipBubble
                    onClick()
                }
        ) {
            Canvas(modifier = Modifier.size(width = 68.dp, height = 58.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Robot Head Center
                val headCenterX = canvasWidth / 2f
                val headCenterY = 28f
                val headRadiusX = 26f
                val headRadiusY = 20f

                // 1. Glowing Lotus Bud Antenna on top (matches MindNest logo)
                val antennaBaseY = headCenterY - headRadiusY
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFF58DDB7).copy(alpha = antennaGlow),
                            Color(0xFFB88DF7).copy(alpha = 0.2f)
                        ),
                        center = Offset(headCenterX, antennaBaseY - 6f),
                        radius = 8f
                    ),
                    center = Offset(headCenterX, antennaBaseY - 6f),
                    radius = 5.5f
                )
                // Antenna stem
                drawLine(
                    color = Color(0xFFC7CBD1),
                    start = Offset(headCenterX, antennaBaseY),
                    end = Offset(headCenterX, antennaBaseY - 4f),
                    strokeWidth = 2.5f
                )

                // 2. Ear Sensors (soft lavender rounded caps)
                drawRoundRect(
                    color = Color(0xFF8E81BC),
                    topLeft = Offset(headCenterX - headRadiusX - 4f, headCenterY - 6f),
                    size = Size(5f, 12f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = Color(0xFF8E81BC),
                    topLeft = Offset(headCenterX + headRadiusX - 1f, headCenterY - 6f),
                    size = Size(5f, 12f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // 3. Robot Head Body (Sleek porcelain white with gentle gradient)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F5F2), Color(0xFFE2EDE7))
                    ),
                    topLeft = Offset(headCenterX - headRadiusX, headCenterY - headRadiusY),
                    size = Size(headRadiusX * 2, headRadiusY * 2),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                // Head border
                drawRoundRect(
                    color = Color(0xFFD3DCD6),
                    topLeft = Offset(headCenterX - headRadiusX, headCenterY - headRadiusY),
                    size = Size(headRadiusX * 2, headRadiusY * 2),
                    cornerRadius = CornerRadius(14f, 14f),
                    style = Stroke(width = 1.2f)
                )

                // 4. Glossy Visor / Screen Face (Deep charcoal teal)
                val visorLeft = headCenterX - 18f
                val visorTop = headCenterY - 10f
                val visorWidth = 36f
                val visorHeight = 20f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF26322C), Color(0xFF1E2623))
                    ),
                    topLeft = Offset(visorLeft, visorTop),
                    size = Size(visorWidth, visorHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Visor soft glass highlight reflection
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(visorLeft + 2f, visorTop + 2f),
                    size = Size(visorWidth - 4f, 6f),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // 5. Digital Friendly Glowing Eyes (Animated blinking!)
                val eyeWidth = 5.5f
                val eyeHeight = 6f * eyeBlinkScale
                val eyeLeftX = headCenterX - 9f
                val eyeRightX = headCenterX + 9f
                val eyeY = headCenterY - (eyeHeight / 2f)

                val eyeColor = if (isThinking) Color(0xFFF39C12) else Color(0xFF4EEAC3)

                // Left Eye
                drawRoundRect(
                    color = eyeColor,
                    topLeft = Offset(eyeLeftX - eyeWidth / 2f, eyeY),
                    size = Size(eyeWidth, eyeHeight),
                    cornerRadius = CornerRadius(2.5f, 2.5f)
                )
                // Right Eye
                drawRoundRect(
                    color = eyeColor,
                    topLeft = Offset(eyeRightX - eyeWidth / 2f, eyeY),
                    size = Size(eyeWidth, eyeHeight),
                    cornerRadius = CornerRadius(2.5f, 2.5f)
                )

                // Subtle blush cheeks (peach)
                drawCircle(
                    color = Color(0xFFFFA07A).copy(alpha = 0.45f),
                    center = Offset(headCenterX - 14f, headCenterY + 4f),
                    radius = 2.2f
                )
                drawCircle(
                    color = Color(0xFFFFA07A).copy(alpha = 0.45f),
                    center = Offset(headCenterX + 14f, headCenterY + 4f),
                    radius = 2.2f
                )

                // 6. Tiny Cute Curved Robotic Hands "Hanging" / Curling over the Chatbox Edge!
                // Left Hand curled over the top border
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFD6E4DC))
                    ),
                    topLeft = Offset(headCenterX - 19f, canvasHeight - 16f),
                    size = Size(9f, 13f),
                    cornerRadius = CornerRadius(4.5f, 4.5f)
                )
                drawRoundRect(
                    color = Color(0xFFB4C2BA),
                    topLeft = Offset(headCenterX - 19f, canvasHeight - 16f),
                    size = Size(9f, 13f),
                    cornerRadius = CornerRadius(4.5f, 4.5f),
                    style = Stroke(width = 1f)
                )

                // Right Hand curled over the top border
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFD6E4DC))
                    ),
                    topLeft = Offset(headCenterX + 10f, canvasHeight - 16f),
                    size = Size(9f, 13f),
                    cornerRadius = CornerRadius(4.5f, 4.5f)
                )
                drawRoundRect(
                    color = Color(0xFFB4C2BA),
                    topLeft = Offset(headCenterX + 10f, canvasHeight - 16f),
                    size = Size(9f, 13f),
                    cornerRadius = CornerRadius(4.5f, 4.5f),
                    style = Stroke(width = 1f)
                )
            }
        }
    }
}
