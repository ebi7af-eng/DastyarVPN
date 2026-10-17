package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.model.TrafficStats
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BandwidthAndTimerView(
    status: ConnectionStatus,
    trafficStats: TrafficStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connected Timer: only visible in Connected state
        AnimatedVisibility(
            visible = status == ConnectionStatus.Connected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = trafficStats.formattedDuration,
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // Upload and Download Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Upload Metric
            SpeedMetricItem(
                isUpload = true,
                speed = trafficStats.uploadSpeed,
                unit = trafficStats.uploadUnit
            )

            // Download Metric
            SpeedMetricItem(
                isUpload = false,
                speed = trafficStats.downloadSpeed,
                unit = trafficStats.downloadUnit
            )
        }
    }
}

@Composable
private fun SpeedMetricItem(
    isUpload: Boolean,
    speed: String,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Arrow Icon
        Icon(
            imageVector = if (isUpload) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            contentDescription = if (isUpload) "Upload" else "Download",
            tint = Color(0xFFC0C2D0),
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = if (isUpload) "Upload" else "Download",
                color = TextSecondary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Normal
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = speed,
                    color = TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
