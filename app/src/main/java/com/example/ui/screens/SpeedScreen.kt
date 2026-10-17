package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnUiState

@Composable
fun SpeedScreen(
    state: VpnUiState,
    onStartTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Network Diagnostics",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Connected via ${state.activeConfig.country} (${state.activeConfig.server})",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Animated Gauge Dial
        val animatedProgress by animateFloatAsState(
            targetValue = state.speedTestProgress,
            label = "gauge_progress"
        )

        Box(
            modifier = Modifier
                .size(230.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val diameter = size.width - strokeWidth
                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                val arcSize = Size(diameter, diameter)

                // Background arc (240 degrees gauge)
                drawArc(
                    color = Color(0xFF22242E),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active progress arc
                if (animatedProgress > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color(0xFFB5EF1E), NeonLime, Color(0xFFE4FF58))
                        ),
                        startAngle = 150f,
                        sweepAngle = 240f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (state.speedTestDownload != "--") state.speedTestDownload else "Ready",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Download Speed",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Metrics Grid (Ping, Jitter, Upload, Download)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedStatCard(
                title = "Ping",
                value = if (state.speedTestPing != "--") state.speedTestPing else "${state.activeConfig.pingMs ?: 76} ms",
                icon = Icons.Default.NetworkCheck,
                modifier = Modifier.weight(1f)
            )

            SpeedStatCard(
                title = "Jitter",
                value = if (state.speedTestJitter != "--") state.speedTestJitter else "3 ms",
                icon = Icons.Default.NetworkCheck,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedStatCard(
                title = "Upload Peak",
                value = if (state.speedTestUpload != "--") state.speedTestUpload else "${state.trafficStats.uploadSpeed} ${state.trafficStats.uploadUnit}",
                icon = Icons.Default.ArrowUpward,
                modifier = Modifier.weight(1f)
            )

            SpeedStatCard(
                title = "Download Peak",
                value = if (state.speedTestDownload != "--") state.speedTestDownload else "${state.trafficStats.downloadSpeed} ${state.trafficStats.downloadUnit}",
                icon = Icons.Default.ArrowDownward,
                modifier = Modifier.weight(1f)
            )
        }

        // Action Button
        Button(
            onClick = onStartTest,
            enabled = !state.isSpeedTestRunning,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_speed_test_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonLime,
                contentColor = Color(0xFF101316)
            )
        ) {
            Text(
                text = if (state.isSpeedTestRunning) "Testing Speed..." else "Start Speed Test",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SpeedStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DarkCard)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
