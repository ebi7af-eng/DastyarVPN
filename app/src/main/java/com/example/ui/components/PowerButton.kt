package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark

@Composable
fun PowerButton(
    status: ConnectionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 175.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    // Infinite spinner animation for connecting state
    val infiniteTransition = rememberInfiniteTransition(label = "connecting_anim")
    val spinnerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_angle"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    // Dynamic colors based on connection state
    val isConnected = status == ConnectionStatus.Connected
    val isConnecting = status == ConnectionStatus.Connecting

    val textColor by animateColorAsState(
        targetValue = when (status) {
            ConnectionStatus.NotConnected -> Color(0xFF8F919E)
            ConnectionStatus.Connecting -> NeonLime
            ConnectionStatus.Connected -> Color(0xFF111417)
        },
        label = "btn_text_color"
    )

    val iconColor by animateColorAsState(
        targetValue = when (status) {
            ConnectionStatus.NotConnected -> Color(0xFF8F919E)
            ConnectionStatus.Connecting -> NeonLime
            ConnectionStatus.Connected -> Color(0xFF111417)
        },
        label = "btn_icon_color"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .testTag("vpn_power_button")
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background and border drawing
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f

            when (status) {
                ConnectionStatus.NotConnected -> {
                    // Dark radial gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2C2E38),
                                Color(0xFF1C1D24),
                                Color(0xFF14151B)
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                    // Outer border
                    drawCircle(
                        color = Color(0xFF2E313D),
                        radius = radius - 1.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                ConnectionStatus.Connecting -> {
                    // Dark button with glowing spinning ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2E313B),
                                Color(0xFF1E2028),
                                Color(0xFF15161D)
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                    // Spinning glowing border arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                NeonLime.copy(alpha = 0.2f),
                                NeonLime
                            )
                        ),
                        startAngle = spinnerAngle,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                        size = Size(this.size.width - 4.dp.toPx(), this.size.height - 4.dp.toPx()),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                ConnectionStatus.Connected -> {
                    // Glowing Neon Lime button
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE4FF58),
                                NeonLime,
                                NeonLimeDark
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                    // Bright rim
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = radius - 1.5.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }

        // Button Content: Power Symbol and Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Power icon drawn custom with Canvas for clean geometry
            Canvas(modifier = Modifier.size(46.dp)) {
                val iconW = this.size.width
                val iconH = this.size.height
                val center = Offset(iconW / 2f, iconH / 2f + 2.dp.toPx())
                val arcRadius = iconW * 0.38f
                val strokeW = 4.2.dp.toPx()

                // Standby arc (leaving open gap at the top, e.g., 60 deg opening)
                drawArc(
                    color = iconColor,
                    startAngle = 300f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )

                // Vertical top bar
                drawLine(
                    color = iconColor,
                    start = Offset(center.x, center.y - arcRadius - 4.dp.toPx()),
                    end = Offset(center.x, center.y - 2.dp.toPx()),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val labelText = when (status) {
                ConnectionStatus.NotConnected -> "Tap to connect"
                ConnectionStatus.Connecting -> "Connecting"
                ConnectionStatus.Connected -> "Disconnect"
            }

            Text(
                text = labelText,
                color = textColor,
                fontSize = 13.5.sp,
                fontWeight = if (isConnected || isConnecting) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
