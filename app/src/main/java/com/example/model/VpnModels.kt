package com.example.model

enum class ConnectionStatus {
    NotConnected,
    Connecting,
    Connected
}

enum class V2rayProtocol(val displayName: String) {
    VLESS("VLESS"),
    VMESS("VMESS"),
    TROJAN("Trojan"),
    SHADOWSOCKS("Shadowsocks"),
    CUSTOM("Custom")
}

data class V2rayServerConfig(
    val remarks: String = "United States",
    val protocol: V2rayProtocol = V2rayProtocol.VLESS,
    val server: String = "37.120.202.186",
    val port: Int = 443,
    val country: String = "United States",
    val countryCode: String = "US",
    val pingMs: Int = 76,
    val rawConfig: String = "vless://a4c281df-8822-4411-92b0-81f7293a11b9@37.120.202.186:443?security=reality&sni=gateway.icloud.com&type=tcp#United%20States"
)

enum class BottomNavTab {
    Home,
    Speed,
    Profile
}

data class TrafficStats(
    val uploadSpeed: String = "0",
    val uploadUnit: String = "KB/s",
    val downloadSpeed: String = "0",
    val downloadUnit: String = "KB/s",
    val durationSeconds: Long = 0L
) {
    val formattedDuration: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return "%02d:%02d:%02d".format(hours, minutes, seconds)
        }
}
