package org.dimalei.wiimvolumesync.viewmodel

import android.net.InetAddresses
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig
import org.dimalei.wiimvolumesync.data.getPlayerStatus
import org.dimalei.wiimvolumesync.data.getServerPublicKeyPin


class VolumeSyncModel(private val config: VolumeSyncConfig) : ViewModel() {

    val tag = this.javaClass.simpleName

    var log by mutableStateOf("")

    var wiimIpAddress by mutableStateOf("")
    var wiimMaxVol by mutableStateOf("50")
    lateinit var keyHash: String


    val ipHasErrors by derivedStateOf {
        !InetAddresses.isNumericAddress(wiimIpAddress)
    }

    val maxVolHasErrors by derivedStateOf {
        !(wiimMaxVol.isNotBlank() && wiimMaxVol.isDigitsOnly() && Integer.valueOf(wiimMaxVol.trim()) in 0..99)
    }

    fun fetchConfig() {
        viewModelScope.launch {
            val ip = config.wiimAddressFlow.first()
            val max = config.maxVolumeFlow.first()
            val pinBase = config.pinBaseFlow.first()

            Log.d(tag, "fetched ip = $ip")
            Log.d(tag, "max vpl = $max")
            Log.d(tag, "pinBase vpl = $pinBase")


            wiimIpAddress = ip
            wiimMaxVol = max.toString()
            keyHash = pinBase
        }
    }

    private fun logMessage(message: String) {
        this.log += "> $message\n"
    }

    fun verifyAndApply() {
        if (ipHasErrors || maxVolHasErrors) {
            logMessage("Your input has errors.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getPublicKeyPin()
                apply()
                test()
                logMessage("✅ Success")
            } catch (e: Exception) {
                logMessage(e.message ?: "Unknown error: ${e.toString()}")
            }
        }
    }

    private fun getPublicKeyPin() {
        getServerPublicKeyPin(
            host = wiimIpAddress,
            onSuccess = {
                keyHash = it
                logMessage("SSLKeyHash extracted: $it")
            },
            onError = { throw it }
        )
    }

    suspend fun apply() {
        val vol = wiimMaxVol.toIntOrNull() ?: 50

        config.storeConfig(
            wiimAddress = wiimIpAddress,
            maxVol = vol,
            pinBase = keyHash
        )
        logMessage("Settings Applied: wiimAddress=$wiimIpAddress, maxVol=$vol, pinBase=$keyHash")
    }

    private fun test() {
        getPlayerStatus(
            ipAddress = wiimIpAddress,
            expectedPinBase64 = keyHash,
            onSuccess = { logMessage("Device Found: " + it.getString("DeviceName")) },
            onError = { throw it }
        )
    }

    fun testManually() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                test()
            } catch (e: Exception) {
                logMessage(e.message ?: "Unknown error: ${e.toString()}")
            }
        }
    }
}