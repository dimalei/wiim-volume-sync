package org.dimalei.wiimvolumesync.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConfigUpdater(
    context: Context,
    onIpUpdated: (String) -> Unit,
    onPinBaseUpdated: (String) -> Unit,
    onVolumeStepUpdate: (Int) -> Unit,
) {
    val TAG = javaClass.simpleName
    var config = VolumeSyncConfig(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            config.wiimAddressFlow.collectLatest {
                onIpUpdated(it)
                Log.d(TAG, "ip updated $it")
            }
        }
        scope.launch {
            config.pinBaseFlow.collectLatest {
                onPinBaseUpdated(it)
                Log.d(TAG, "pinBase updated $it")
            }
        }
        scope.launch {
            config.volumeStepFlow.collectLatest {
                onVolumeStepUpdate(it)
                Log.d(TAG, "volStep updated $it")
            }
        }
    }

    suspend fun getIp(): String {
        return config.wiimAddressFlow.first()
    }

    suspend fun getPinBase(): String {
        return config.pinBaseFlow.first()
    }

    suspend fun getVolStep(): Int {
        return config.volumeStepFlow.first()
    }

    fun apply(ip: String, volStep: Int, keyHash: String) {
        scope.launch {
            config.storeConfig(
                wiimAddress = ip,
                volumeStep = volStep,
                pinBase = keyHash
            )
        }
    }
}