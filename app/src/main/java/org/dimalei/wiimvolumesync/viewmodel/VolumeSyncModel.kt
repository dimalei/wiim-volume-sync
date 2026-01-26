package org.dimalei.wiimvolumesync.viewmodel

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig


class VolumeSyncModel(private val config: VolumeSyncConfig) : ViewModel() {

    val tag = this.javaClass.simpleName


    var ipTextFieldState = TextFieldState()
    var maxVolTextFieldState = TextFieldState()

    fun init() {
        viewModelScope.launch {
            val ip = config.wiimAddressFlow.first()
            val max = config.maxVolumeFlow.first()

            Log.d(tag, "fetched ip = $ip")
            Log.d(tag, "max vpl = $max")

            ipTextFieldState.edit {
                replace(0, length, ip)
            }

            maxVolTextFieldState.edit {
                replace(0, length, max.toString())
            }
        }
    }

    fun apply() {
        viewModelScope.launch {
            config.storeConfig(
                wiimAddress = ipTextFieldState.text.toString(),
                maxVol = maxVolTextFieldState.text.toString().toIntOrNull() ?: 50
            )
        }
    }

}