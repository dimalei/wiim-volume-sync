package org.dimalei.wiimvolumesync.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig

class VolumeSync : Service() {
    val tag: String = this.javaClass.simpleName

    // Config
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var volumeSyncConfig: VolumeSyncConfig
    private var configJob: Job? = null

    var wiimIp: String? = null
    var maxVol: Int? = null

    // Volume Observer
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var settingsObserver: SettingsContentObserver? = null

    private class ServiceHandler(looper: Looper) : Handler(looper) {

    }

    override fun onCreate() {
        // auto update config
        volumeSyncConfig = VolumeSyncConfig(applicationContext)
        serviceScope.launch {
            volumeSyncConfig.wiimAddressFlow.collectLatest {
                wiimIp = it
                Log.i(tag, "ip updated $wiimIp")
            }
            volumeSyncConfig.maxVolumeFlow.collectLatest {
                maxVol = it
                Log.i(tag, "maxVol updated $maxVol")
            }
        }

        val thread = HandlerThread(
            "VolumeSyncThread",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        thread.start()

        serviceLooper = thread.looper
        serviceHandler = ServiceHandler(thread.looper)

        settingsObserver = SettingsContentObserver(
            context = this,
            handler = serviceHandler!!
        )

        contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            settingsObserver!!
        )

        Log.d(tag, "Volume Sync Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(tag, "Volume Sync Service starting ...")

        // If we get killed, after returning from here, restart
        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        // no binding
        return null
    }

    override fun onDestroy() {
        configJob?.cancel()

        settingsObserver?.let { contentResolver.unregisterContentObserver(it) }
        settingsObserver = null

        serviceLooper?.quitSafely()
        serviceLooper = null
        serviceHandler = null

        Log.i(tag, "Service Destroyed")
    }
}