package org.dimalei.wiimvolumesync.viewmodel

import android.content.Context
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
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.ConfigUpdater
import org.dimalei.wiimvolumesync.data.WiimConnector
import org.dimalei.wiimvolumesync.data.getServerPublicKeyPin


val MAX_VOL_STEP = 10
val MIN_VOL_STEP = 1

class VolumeSyncModel(context: Context) : ViewModel() {

    val tag = this.javaClass.simpleName
    var log by mutableStateOf("")


    // control
    val wiimConnector = WiimConnector(context)

    // config
    val configUpdater = ConfigUpdater(
        context = context,
        onIpUpdated = { ip = it },
        onPinBaseUpdated = { keyHash = it },
        onVolumeStepUpdate = { volStep = it.toString() }
    )


    // input
    var ip by mutableStateOf("")
    var volStep by mutableStateOf("2")
    lateinit var keyHash: String

    val ipHasErrors by derivedStateOf {
        !InetAddresses.isNumericAddress(ip)
    }
    val volumeStepHasErrors by derivedStateOf {
        !(volStep.isNotBlank() && volStep.isDigitsOnly() && Integer.valueOf(volStep.trim()) in MIN_VOL_STEP..MAX_VOL_STEP)
    }
    val volumeStepErrorMessage = "Must be $MIN_VOL_STEP - $MAX_VOL_STEP"

    fun fetchConfig() {
        viewModelScope.launch {
            ip = configUpdater.getIp()
            volStep = configUpdater.getVolStep().toString()
            keyHash = configUpdater.getPinBase()

            Log.d(
                tag,
                "settings updated ip: $ip, volStep: $volStep, pinBase: $keyHash"
            )
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
                logMessage("Success ✔\uFE0F")
            } catch (e: Exception) {
                logMessage(e.message ?: "Unknown error: ${e.toString()}")
            }
        }
    }

    private fun getPublicKeyPin() {
        getServerPublicKeyPin(
            host = ip,
            onSuccess = {
                keyHash = it
                logMessage("SSLKeyHash extracted: $it")
            },
            onError = { throw it }
        )
    }

    private fun apply() {
        configUpdater.apply(
            ip = ip,
            volStep = volStep.toIntOrNull() ?: 2,
            keyHash = keyHash
        )
    }

    private fun test() {
        wiimConnector.getDeviceStatus(
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