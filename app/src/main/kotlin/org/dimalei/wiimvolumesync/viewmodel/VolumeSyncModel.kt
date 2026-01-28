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
import org.dimalei.wiimvolumesync.data.getDeviceStatus
import org.dimalei.wiimvolumesync.data.getServerPublicKeyPin


val MAX_VOL_STEP = 10
val MIN_VOL_STEP = 1

class VolumeSyncModel(private val config: VolumeSyncConfig) : ViewModel() {


    val tag = this.javaClass.simpleName

    var log by mutableStateOf("")

    var wiimIpAddress by mutableStateOf("")
    var volumeStep by mutableStateOf("2")
    lateinit var keyHash: String


    val ipHasErrors by derivedStateOf {
        !InetAddresses.isNumericAddress(wiimIpAddress)
    }

    val volumeStepHasErrors by derivedStateOf {
        !(volumeStep.isNotBlank() && volumeStep.isDigitsOnly() && Integer.valueOf(volumeStep.trim()) in MIN_VOL_STEP..MAX_VOL_STEP)
    }
    val volumeStepErrorMessage = "Must be $MIN_VOL_STEP - $MAX_VOL_STEP"

    fun fetchConfig() {
        viewModelScope.launch {
            val ip = config.wiimAddressFlow.first()
            val volStep = config.maxVolumeFlow.first()
            val pinBase = config.pinBaseFlow.first()

            Log.d(tag, "fetched ip = $ip")
            Log.d(tag, "volume step = $volStep")
            Log.d(tag, "pinBase = $pinBase")


            wiimIpAddress = ip
            volumeStep = volStep.toString()
            keyHash = pinBase
        }
    }

    private fun logMessage(message: String) {
        this.log += "> $message\n"
    }

    fun verifyAndApply() {
        if (ipHasErrors || volumeStepHasErrors) {
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
        val volumeStep = this@VolumeSyncModel.volumeStep.toIntOrNull() ?: 2

        config.storeConfig(
            wiimAddress = wiimIpAddress,
            volumeStep = volumeStep,
            pinBase = keyHash
        )
        logMessage("Settings Applied: wiimAddress=$wiimIpAddress, volumeStep=$volumeStep, pinBase=$keyHash")
    }

    private fun test() {
        getDeviceStatus(
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