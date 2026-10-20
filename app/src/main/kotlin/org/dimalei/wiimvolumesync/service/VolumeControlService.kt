package org.dimalei.wiimvolumesync.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.dimalei.wiimvolumesync.data.wiim.WiimConnector


@SuppressLint("AccessibilityPolicy")
class VolumeControlService : AccessibilityService() {
    val TAG = javaClass.simpleName
    lateinit var wiimConnector: WiimConnector
    lateinit var settingsRepository: SettingsRepository


    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        wiimConnector = WiimConnector(settingsRepository)
        Log.d(TAG, "Service Created")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    public override fun onKeyEvent(event: KeyEvent): Boolean {

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP &&
            event.action == KeyEvent.ACTION_DOWN
        ) {
            Log.d(TAG, "Volume +")
            wiimConnector.changePlayerVolume(WiimConnector.Volume.UP)
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN &&
            event.action == KeyEvent.ACTION_DOWN
        ) {
            Log.d(TAG, "Volume -")
            wiimConnector.changePlayerVolume(WiimConnector.Volume.DOWN)
            return true
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted.")
    }
}

