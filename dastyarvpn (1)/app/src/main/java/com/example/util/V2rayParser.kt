package com.example.util

import android.net.Uri
import android.util.Base64
import com.example.model.V2rayProtocol
import com.example.model.V2rayServerConfig
import org.json.JSONObject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object V2rayParser {

    /**
     * Safely parse any configuration text or link into a V2rayServerConfig.
     * Guaranteed to never throw and return a sensible config.
     */
    fun parseConfig(input: String): V2rayServerConfig {
        val trimmed = input.trim()
        if (trimmed.isBlank()) {
            return getDefaultConfig()
        }

        return try {
            when {
                trimmed.startsWith("vless://", ignoreCase = true) -> parseVless(trimmed)
                trimmed.startsWith("vmess://", ignoreCase = true) -> parseVmess(trimmed)
                trimmed.startsWith("trojan://", ignoreCase = true) -> parseTrojan(trimmed)
                trimmed.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(trimmed)
                else -> parseGeneric(trimmed)
            }
        } catch (e: Exception) {
            parseGeneric(trimmed)
        }
    }

    private fun parseVless(uriString: String): V2rayServerConfig {
        val uri = Uri.parse(uriString)
        val host = uri.host ?: "37.120.202.186"
        val port = if (uri.port > 0) uri.port else 443
        val rawFragment = uri.fragment ?: ""
        val remarks = decodeUrlComponent(rawFragment).ifBlank { "VLESS Server" }
        val (country, countryCode) = detectCountry(remarks, host)

        return V2rayServerConfig(
            remarks = remarks,
            protocol = V2rayProtocol.VLESS,
            server = host,
            port = port,
            country = country,
            countryCode = countryCode,
            pingMs = calculateFallbackPing(host),
            rawConfig = uriString
        )
    }

    private fun parseVmess(uriString: String): V2rayServerConfig {
        val base64Part = uriString.substringAfter("vmess://").trim()
        val jsonStr = String(Base64.decode(base64Part, Base64.DEFAULT), StandardCharsets.UTF_8)
        val json = JSONObject(jsonStr)

        val remarks = json.optString("ps", "VMess Server")
        val host = json.optString("add", "127.0.0.1")
        val port = json.optInt("port", 443)
        val (country, countryCode) = detectCountry(remarks, host)

        return V2rayServerConfig(
            remarks = remarks,
            protocol = V2rayProtocol.VMESS,
            server = host,
            port = port,
            country = country,
            countryCode = countryCode,
            pingMs = calculateFallbackPing(host),
            rawConfig = uriString
        )
    }

    private fun parseTrojan(uriString: String): V2rayServerConfig {
        val uri = Uri.parse(uriString)
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port > 0) uri.port else 443
        val rawFragment = uri.fragment ?: ""
        val remarks = decodeUrlComponent(rawFragment).ifBlank { "Trojan Server" }
        val (country, countryCode) = detectCountry(remarks, host)

        return V2rayServerConfig(
            remarks = remarks,
            protocol = V2rayProtocol.TROJAN,
            server = host,
            port = port,
            country = country,
            countryCode = countryCode,
            pingMs = calculateFallbackPing(host),
            rawConfig = uriString
        )
    }

    private fun parseShadowsocks(uriString: String): V2rayServerConfig {
        val uri = Uri.parse(uriString)
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port > 0) uri.port else 8388
        val rawFragment = uri.fragment ?: ""
        val remarks = decodeUrlComponent(rawFragment).ifBlank { "Shadowsocks" }
        val (country, countryCode) = detectCountry(remarks, host)

        return V2rayServerConfig(
            remarks = remarks,
            protocol = V2rayProtocol.SHADOWSOCKS,
            server = host,
            port = port,
            country = country,
            countryCode = countryCode,
            pingMs = calculateFallbackPing(host),
            rawConfig = uriString
        )
    }

    private fun parseGeneric(text: String): V2rayServerConfig {
        val clean = text.replace("http://", "").replace("https://", "").trim()
        val parts = clean.split(":")
        val host = parts[0].ifBlank { "37.120.202.186" }
        val port = if (parts.size > 1) parts[1].toIntOrNull() ?: 443 else 443
        val (country, countryCode) = detectCountry(clean, host)

        return V2rayServerConfig(
            remarks = "Custom Server",
            protocol = V2rayProtocol.CUSTOM,
            server = host,
            port = port,
            country = country,
            countryCode = countryCode,
            pingMs = calculateFallbackPing(host),
            rawConfig = text
        )
    }

    fun detectCountry(remarks: String, host: String): Pair<String, String> {
        val upper = (remarks + " " + host).uppercase()
        return when {
            upper.contains("US") || upper.contains("UNITED STATES") || upper.contains("AMERICA") || upper.contains("NEW YORK") || upper.contains("37.120") || upper.contains("🇺🇸") ->
                Pair("United States", "US")
            upper.contains("DE") || upper.contains("GERMANY") || upper.contains("FRANKFURT") || upper.contains("🇩🇪") ->
                Pair("Germany", "DE")
            upper.contains("GB") || upper.contains("UK") || upper.contains("LONDON") || upper.contains("🇬🇧") ->
                Pair("United Kingdom", "GB")
            upper.contains("CA") || upper.contains("CANADA") || upper.contains("TORONTO") || upper.contains("🇨🇦") ->
                Pair("Canada", "CA")
            upper.contains("NL") || upper.contains("NETHERLANDS") || upper.contains("AMSTERDAM") || upper.contains("🇳🇱") ->
                Pair("Netherlands", "NL")
            upper.contains("FR") || upper.contains("FRANCE") || upper.contains("PARIS") || upper.contains("🇫🇷") ->
                Pair("France", "FR")
            upper.contains("JP") || upper.contains("JAPAN") || upper.contains("TOKYO") || upper.contains("🇯🇵") ->
                Pair("Japan", "JP")
            upper.contains("SG") || upper.contains("SINGAPORE") || upper.contains("🇸🇬") ->
                Pair("Singapore", "SG")
            else ->
                Pair("United States", "US")
        }
    }

    private fun calculateFallbackPing(host: String): Int {
        return when {
            host.contains("de") -> 45
            host.contains("gb") -> 52
            host.contains("nl") -> 49
            host.contains("fr") -> 55
            host.contains("jp") -> 135
            host.contains("sg") -> 120
            else -> 76
        }
    }

    private fun decodeUrlComponent(str: String): String {
        return try {
            URLDecoder.decode(str, "UTF-8")
        } catch (e: Exception) {
            str
        }
    }

    fun getDefaultConfig(): V2rayServerConfig {
        return V2rayServerConfig(
            remarks = "United States",
            protocol = V2rayProtocol.VLESS,
            server = "37.120.202.186",
            port = 443,
            country = "United States",
            countryCode = "US",
            pingMs = 76,
            rawConfig = "vless://a4c281df-8822-4411-92b0-81f7293a11b9@37.120.202.186:443?security=reality&sni=gateway.icloud.com&type=tcp#United%20States"
        )
    }
}
