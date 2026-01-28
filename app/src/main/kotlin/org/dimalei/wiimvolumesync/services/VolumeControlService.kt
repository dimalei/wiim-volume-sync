package org.dimalei.wiimvolumesync.services

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig
import org.dimalei.wiimvolumesync.data.changePlayerVolume


@SuppressLint("AccessibilityPolicy")
class VolumeControlService : AccessibilityService() {

    val TAG = javaClass.simpleName
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    // config
    private lateinit var volumeSyncConfig: VolumeSyncConfig

    var wiimIp: String? = null
    var volumeStep: Int = 0
    var pinBase: String? = null

    override fun onServiceConnected() {
        Log.d(TAG, "Service Connected")
        fetchConfig()

    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }


    public override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            Log.v(TAG, "Key pressed via accessibility is: " + event.keyCode)
        }

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP &&
            event.action == KeyEvent.ACTION_DOWN
        ) {
            Log.d(TAG, "Volume +")
            changeVolume(Vol.UP)
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN &&
            event.action == KeyEvent.ACTION_DOWN
        ) {
            Log.d(TAG, "Volume -")
            changeVolume(Vol.DOWN)
            return true
        }

        return super.onKeyEvent(event)
    }

    private fun changeVolume(control: Vol) {

        if (wiimIp == null || pinBase == null) {
            Log.d(TAG, "config is missing. abort.")
            return
        }

        val amount = volumeStep * control.factor

        serviceScope.launch(Dispatchers.IO) {
            changePlayerVolume(
                ipAddress = wiimIp!!,
                expectedPinBase64 = pinBase!!,
                amount = amount,
                onSuccess = { Log.d(TAG, "Volume changed $it") },
                onError = { Log.d(TAG, "Volume change failed: \n${it}") }
            )
        }
    }

    enum class Vol(val factor: Int) {
        UP(1),
        DOWN(-1)
    }

    private fun fetchConfig() {
        volumeSyncConfig = VolumeSyncConfig(applicationContext)
        serviceScope.launch {
            volumeSyncConfig.wiimAddressFlow.collectLatest {
                wiimIp = it
                Log.d(TAG, "ip updated $wiimIp")
            }
        }
        serviceScope.launch {
            volumeSyncConfig.maxVolumeFlow.collectLatest {
                volumeStep = it
                Log.d(TAG, "maxVol updated $volumeStep")
            }
        }
        serviceScope.launch {
            volumeSyncConfig.pinBaseFlow.collectLatest {
                pinBase = it
                Log.d(TAG, "pinBase updated $pinBase")
            }
        }
    }


    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted.")
    }

}

