package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BottomNavTab
import com.example.model.ConnectionStatus
import com.example.model.TrafficStats
import com.example.model.V2rayServerConfig
import com.example.util.V2rayParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class VpnUiState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.NotConnected,
    val activeConfig: V2rayServerConfig = V2rayParser.getDefaultConfig(),
    val trafficStats: TrafficStats = TrafficStats(),
    val currentTab: BottomNavTab = BottomNavTab.Home,
    val isEditConfigDialogVisible: Boolean = false,
    val isProModalVisible: Boolean = false,
    val handshakeStatusMessage: String = "",
    val snackbarMessage: String? = null,
    // Speed Test states
    val speedTestProgress: Float = 0f,
    val isSpeedTestRunning: Boolean = false,
    val speedTestDownload: String = "--",
    val speedTestUpload: String = "--",
    val speedTestPing: String = "--",
    val speedTestJitter: String = "--",
    // Settings states
    val protocol: String = "WireGuard / Xray",
    val killSwitchEnabled: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val autoConnectWifi: Boolean = true
)

class VpnViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VpnUiState())
    val uiState: StateFlow<VpnUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var connectionJob: Job? = null
    private var speedTestJob: Job? = null

    fun onPowerButtonClicked() {
        when (_uiState.value.connectionStatus) {
            ConnectionStatus.NotConnected -> startConnecting()
            ConnectionStatus.Connecting -> cancelConnecting()
            ConnectionStatus.Connected -> disconnect()
        }
    }

    fun setForcedStatus(status: ConnectionStatus) {
        connectionJob?.cancel()
        timerJob?.cancel()

        when (status) {
            ConnectionStatus.NotConnected -> {
                _uiState.update {
                    it.copy(
                        connectionStatus = ConnectionStatus.NotConnected,
                        handshakeStatusMessage = "",
                        trafficStats = TrafficStats("0", "KB/s", "0", "KB/s", 0L)
                    )
                }
            }
            ConnectionStatus.Connecting -> {
                _uiState.update {
                    it.copy(
                        connectionStatus = ConnectionStatus.Connecting,
                        handshakeStatusMessage = "Connecting to ${_uiState.value.activeConfig.server}...",
                        trafficStats = TrafficStats("0", "KB/s", "0", "KB/s", 0L)
                    )
                }
            }
            ConnectionStatus.Connected -> {
                _uiState.update {
                    it.copy(
                        connectionStatus = ConnectionStatus.Connected,
                        handshakeStatusMessage = "Connected to ${_uiState.value.activeConfig.server}",
                        trafficStats = TrafficStats("23", "MB/s", "344", "MB/s", 2606L)
                    )
                }
                startLiveTrafficCounter(initialSeconds = 2606L)
            }
        }
    }

    private fun startConnecting() {
        connectionJob?.cancel()
        timerJob?.cancel()

        val active = _uiState.value.activeConfig
        _uiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.Connecting,
                handshakeStatusMessage = "Connecting to ${active.server}...",
                trafficStats = TrafficStats("0", "KB/s", "0", "KB/s", 0L)
            )
        }

        connectionJob = viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(handshakeStatusMessage = "Securing ${active.protocol.displayName} tunnel...") }
            delay(1000)
            _uiState.update {
                it.copy(
                    connectionStatus = ConnectionStatus.Connected,
                    handshakeStatusMessage = "Connected • ${active.server}:${active.port}",
                    trafficStats = TrafficStats("23", "MB/s", "344", "MB/s", 0L)
                )
            }
            startLiveTrafficCounter(initialSeconds = 0L)
        }
    }

    private fun cancelConnecting() {
        connectionJob?.cancel()
        _uiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.NotConnected,
                handshakeStatusMessage = "",
                trafficStats = TrafficStats("0", "KB/s", "0", "KB/s", 0L)
            )
        }
    }

    private fun disconnect() {
        connectionJob?.cancel()
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.NotConnected,
                handshakeStatusMessage = "Disconnected",
                trafficStats = TrafficStats("0", "KB/s", "0", "KB/s", 0L)
            )
        }
    }

    private fun startLiveTrafficCounter(initialSeconds: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = initialSeconds
            while (isActive) {
                delay(1000)
                seconds++
                val upJitter = 21 + Random.nextInt(0, 5)
                val downJitter = 338 + Random.nextInt(0, 16)

                _uiState.update { current ->
                    current.copy(
                        trafficStats = TrafficStats(
                            uploadSpeed = upJitter.toString(),
                            uploadUnit = "MB/s",
                            downloadSpeed = downJitter.toString(),
                            downloadUnit = "MB/s",
                            durationSeconds = seconds
                        )
                    )
                }
            }
        }
    }

    /**
     * Updates the single active configuration safely from user input.
     */
    fun updateConfig(rawInput: String) {
        val parsed = V2rayParser.parseConfig(rawInput)
        val wasConnected = _uiState.value.connectionStatus == ConnectionStatus.Connected

        _uiState.update {
            it.copy(
                activeConfig = parsed,
                isEditConfigDialogVisible = false,
                snackbarMessage = "Server configuration updated: ${parsed.remarks}"
            )
        }

        if (wasConnected) {
            startConnecting()
        }
    }

    fun setEditConfigDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isEditConfigDialogVisible = visible) }
    }

    fun setProModalVisible(visible: Boolean) {
        _uiState.update { it.copy(isProModalVisible = visible) }
    }

    fun selectTab(tab: BottomNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setProtocol(protocol: String) {
        _uiState.update { it.copy(protocol = protocol) }
    }

    fun toggleKillSwitch() {
        _uiState.update { it.copy(killSwitchEnabled = !it.killSwitchEnabled) }
    }

    fun toggleSplitTunneling() {
        _uiState.update { it.copy(splitTunnelingEnabled = !it.splitTunnelingEnabled) }
    }

    fun toggleAutoConnectWifi() {
        _uiState.update { it.copy(autoConnectWifi = !it.autoConnectWifi) }
    }

    fun runSpeedTest() {
        if (_uiState.value.isSpeedTestRunning) return

        speedTestJob?.cancel()
        speedTestJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSpeedTestRunning = true,
                    speedTestProgress = 0f,
                    speedTestDownload = "--",
                    speedTestUpload = "--",
                    speedTestPing = "Testing...",
                    speedTestJitter = "Testing..."
                )
            }

            delay(700)
            val ping = _uiState.value.activeConfig.pingMs
            _uiState.update {
                it.copy(
                    speedTestProgress = 0.25f,
                    speedTestPing = "$ping ms",
                    speedTestJitter = "2 ms"
                )
            }

            for (step in 1..10) {
                delay(120)
                val dlValue = (140 + step * 20 + Random.nextInt(5)).coerceAtMost(354)
                _uiState.update {
                    it.copy(
                        speedTestProgress = 0.25f + (step / 10f) * 0.45f,
                        speedTestDownload = "$dlValue Mbps"
                    )
                }
            }

            for (step in 1..8) {
                delay(120)
                val ulValue = (12 + step * 2 + Random.nextInt(2)).coerceAtMost(25)
                _uiState.update {
                    it.copy(
                        speedTestProgress = 0.70f + (step / 8f) * 0.30f,
                        speedTestUpload = "$ulValue Mbps"
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isSpeedTestRunning = false,
                    speedTestProgress = 1f,
                    speedTestDownload = "344 Mbps",
                    speedTestUpload = "24 Mbps"
                )
            }
        }
    }
}
