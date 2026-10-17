package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.components.BandwidthAndTimerView
import com.example.ui.components.EditConfigDialog
import com.example.ui.components.HeaderView
import com.example.ui.components.ParticleRing
import com.example.ui.components.PowerButton
import com.example.ui.components.ServerCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.NeonLime
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnUiState
import com.example.viewmodel.VpnViewModel

@Composable
fun HomeScreen(
    state: VpnUiState,
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Quick State Inspector Selector (for instant previewing between all 3 design states)
            StateSwitcherBar(
                currentStatus = state.connectionStatus,
                onStatusSelected = { viewModel.setForcedStatus(it) }
            )

            // Header: Status Connection (Left) and PRO crown badge (Right)
            HeaderView(
                status = state.connectionStatus,
                onProClick = { viewModel.setProModalVisible(true) },
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Handshake / Tunnel Status text if connecting
            AnimatedVisibility(visible = state.handshakeStatusMessage.isNotBlank()) {
                Text(
                    text = state.handshakeStatusMessage,
                    color = NeonLime,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Live Timer (when connected) & Upload / Download Speed Metrics
            BandwidthAndTimerView(
                status = state.connectionStatus,
                trafficStats = state.trafficStats,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Center Area: Animated Particle Ring & Main Power Button
        Box(
            modifier = Modifier
                .size(330.dp)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Organic glowing particle cloud ring
            ParticleRing(
                status = state.connectionStatus,
                modifier = Modifier.fillMaxSize()
            )

            // Main circular connection action button
            PowerButton(
                status = state.connectionStatus,
                onClick = { viewModel.onPowerButtonClicked() },
                size = 175.dp
            )
        }

        // Bottom Selected Server Pill Card: Tapping opens clean manual config editor
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            ServerCard(
                server = state.activeConfig,
                onClick = { viewModel.setEditConfigDialogVisible(true) }
            )
        }
    }

    // Manual Configuration Editor Dialog
    if (state.isEditConfigDialogVisible) {
        EditConfigDialog(
            initialConfig = state.activeConfig.rawConfig,
            onSave = { viewModel.updateConfig(it) },
            onDismiss = { viewModel.setEditConfigDialogVisible(false) }
        )
    }
}

/**
 * Segmented pill row allowing direct switching between the 3 states from the user image:
 * [ Not Connected ] [ Connecting ] [ Connected ]
 */
@Composable
private fun StateSwitcherBar(
    currentStatus: ConnectionStatus,
    onStatusSelected: (ConnectionStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF14151C))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatePillItem(
            title = "Not Connected",
            isSelected = currentStatus == ConnectionStatus.NotConnected,
            onClick = { onStatusSelected(ConnectionStatus.NotConnected) },
            modifier = Modifier.weight(1f)
        )

        StatePillItem(
            title = "Connecting",
            isSelected = currentStatus == ConnectionStatus.Connecting,
            onClick = { onStatusSelected(ConnectionStatus.Connecting) },
            modifier = Modifier.weight(1f)
        )

        StatePillItem(
            title = "Connected",
            isSelected = currentStatus == ConnectionStatus.Connected,
            onClick = { onStatusSelected(ConnectionStatus.Connected) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatePillItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NeonLime.copy(alpha = 0.18f) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) NeonLime else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
