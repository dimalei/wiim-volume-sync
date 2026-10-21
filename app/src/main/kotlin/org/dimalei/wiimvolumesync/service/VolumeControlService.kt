package org.dimalei.wiimvolumesync.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import org.dimalei.wiimvolumesync.data.netflixbutton.NetflixButtonHandler
import org.dimalei.wiimvolumesync.data.settings.GuiLogger.log
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.dimalei.wiimvolumesync.data.wiim.WiimConnector


@SuppressLint("AccessibilityPolicy")
class VolumeControlService : AccessibilityService() {
    val tag: String = javaClass.simpleName
    lateinit var wiimConnector: WiimConnector
    lateinit var settingsRepository: SettingsRepository
    lateinit var netflixButtonHandler: NetflixButtonHandler


    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        wiimConnector = WiimConnector(settingsRepository)
        netflixButtonHandler = NetflixButtonHandler(settingsRepository)
        log(tag, "Service Created")
        Log.d(tag, "Service Created")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    public override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    wiimConnector.changePlayerVolume(WiimConnector.Volume.UP)
                    return true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    wiimConnector.changePlayerVolume(WiimConnector.Volume.DOWN)
                    return true
                }

                KeyEvent.KEYCODE_BUTTON_12 -> {
                    netflixButtonHandler.handle()
                    return true;
                }
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
        log(tag, "Service interrupted.")
        Log.d(tag, "Service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        log(tag, "Service destroyed.")
        Log.d(tag, "Service destroyed.")
    }
}

