package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonLime
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeaderView(
    status: ConnectionStatus,
    onProClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Connection status icon + titles
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Circle
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .then(
                        when (status) {
                            ConnectionStatus.NotConnected -> Modifier.border(1.2.dp, DarkCardBorder, CircleShape)
                            ConnectionStatus.Connecting -> Modifier.border(1.2.dp, NeonLime.copy(alpha = 0.6f), CircleShape)
                            ConnectionStatus.Connected -> Modifier.background(SignalGreen)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    ConnectionStatus.NotConnected -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Not Connected",
                            tint = Color(0xFFA5A7B4),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    ConnectionStatus.Connecting -> {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse_header")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 600),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(NeonLime.copy(alpha = pulseAlpha))
                        )
                    }
                    ConnectionStatus.Connected -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Connected",
                            tint = Color(0xFF0F1216),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Status Connection",
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal
                )
                AnimatedContent(
                    targetState = status,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "status_text"
                ) { targetStatus ->
                    val statusText = when (targetStatus) {
                        ConnectionStatus.NotConnected -> "Not Connected"
                        ConnectionStatus.Connecting -> "Connecting"
                        ConnectionStatus.Connected -> "Connected"
                    }
                    Text(
                        text = statusText,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Right side: PRO badge with Crown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .testTag("pro_badge_button")
                .clip(CircleShape)
                .clickable { onProClick() }
                .padding(4.dp)
        ) {
            Text(
                text = "PRO",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Neon Lime/Gold Crown circular badge
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(NeonLime),
                contentAlignment = Alignment.Center
            ) {
                // Precise Crown silhouette
                Canvas(modifier = Modifier.size(17.dp)) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, h * 0.25f)
                        lineTo(w * 0.28f, h * 0.65f)
                        lineTo(w * 0.5f, 0f)
                        lineTo(w * 0.72f, h * 0.65f)
                        lineTo(w, h * 0.25f)
                        lineTo(w * 0.88f, h)
                        lineTo(w * 0.12f, h)
                        close()
                    }
                    drawPath(path, color = Color(0xFF101316))
                }
            }
        }
    }
}
