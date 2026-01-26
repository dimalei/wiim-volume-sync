package org.dimalei.wiimvolumesync.viewmodel

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
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

    val logMessages = mutableStateOf<List<String>>(ArrayList())

    var ipTextFieldState = TextFieldState()
    var maxVolTextFieldState = TextFieldState()
    var pinBaseFieldState = TextFieldState()

    fun fetchConfig() {
        viewModelScope.launch {
            val ip = config.wiimAddressFlow.first()
            val max = config.maxVolumeFlow.first()
            val pinBase = config.pinBaseFlow.first()

            Log.d(tag, "fetched ip = $ip")
            Log.d(tag, "max vpl = $max")
            Log.d(tag, "pinBase vpl = $pinBase")


            ipTextFieldState.edit {
                replace(0, length, ip)
            }

            maxVolTextFieldState.edit {
                replace(0, length, max.toString())
            }

            pinBaseFieldState.edit {
                replace(0, length, pinBase)
            }
        }
    }

    fun apply() {
        // TODO verify input

        viewModelScope.launch {
            config.storeConfig(
                wiimAddress = ipTextFieldState.text.toString(),
                maxVol = maxVolTextFieldState.text.toString().toIntOrNull() ?: 50,
                pinBase = pinBaseFieldState.text.toString()
            )
        }
        logMessages.value += "Settings Applied"
    }

    fun test() {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO verify input


            val wiimIp = ipTextFieldState.text.toString()
            val pinBase = pinBaseFieldState.text.toString()

            getPlayerStatus(
                ipAddress = wiimIp,
                expectedPinBase64 = pinBase,
                onSuccess = { logMessages.value += "Device Found: " + it.getString("DeviceName") },
                onError = { logMessages.value += it.toString() }
            )
        }
    }

    fun getPublicKeyPin() {
        viewModelScope.launch(Dispatchers.IO) {

            val wiimIp = ipTextFieldState.text.toString()

            getServerPublicKeyPin(
                host = wiimIp,
                onSuccess = {
                    pinBaseFieldState.edit { replace(0, length, it) }
                    logMessages.value += "Public Key Pin fetched"
                },
                onError = { logMessages.value += it.toString() }
            )
        }


    }

}