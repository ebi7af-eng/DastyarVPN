package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ConnectionStatus
import com.example.model.V2rayProtocol
import com.example.util.V2rayParser
import com.example.viewmodel.VpnViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DastyarVPN", appName)
    }

    @Test
    fun `initial vpn state is disconnected`() {
        val viewModel = VpnViewModel()
        val state = viewModel.uiState.value
        assertEquals(ConnectionStatus.NotConnected, state.connectionStatus)
        assertEquals("0", state.trafficStats.uploadSpeed)
        assertEquals("0", state.trafficStats.downloadSpeed)
        assertNotNull(state.activeConfig)
        assertEquals("United States", state.activeConfig.country)
    }

    @Test
    fun `v2ray parser correctly parses vless link`() {
        val uri = "vless://a4c281df-8822-4411-92b0-81f7293a11b9@194.36.108.92:443?security=reality&sni=test.com&type=tcp#Germany%20Frankfurt"
        val config = V2rayParser.parseConfig(uri)
        assertNotNull(config)
        assertEquals(V2rayProtocol.VLESS, config.protocol)
        assertEquals("194.36.108.92", config.server)
        assertEquals(443, config.port)
        assertEquals("Germany", config.country)
        assertEquals("DE", config.countryCode)
    }

    @Test
    fun `update config sets active server safely`() {
        val viewModel = VpnViewModel()
        val uri = "vless://b7d192ae-7711-4522-83b1-92a8304b22c0@51.15.89.201:443?security=reality#France%20Paris"
        viewModel.updateConfig(uri)

        val updatedState = viewModel.uiState.value
        assertEquals("France", updatedState.activeConfig.country)
        assertEquals("51.15.89.201", updatedState.activeConfig.server)
    }

    @Test
    fun `set forced connected state sets active metrics`() {
        val viewModel = VpnViewModel()
        viewModel.setForcedStatus(ConnectionStatus.Connected)
        val state = viewModel.uiState.value
        assertEquals(ConnectionStatus.Connected, state.connectionStatus)
        assertEquals("23", state.trafficStats.uploadSpeed)
        assertEquals("344", state.trafficStats.downloadSpeed)
    }
}
