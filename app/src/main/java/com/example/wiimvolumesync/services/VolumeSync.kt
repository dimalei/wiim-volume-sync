package com.example.wiimvolumesync.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.util.Log
import com.example.wiimvolumesync.data.VolumeSyncConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VolumeSync : Service() {
    val TAG = this.javaClass.simpleName

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

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Volume Sync Service starting ...")


        volumeSyncConfig = VolumeSyncConfig(applicationContext)
        serviceScope.launch {
            volumeSyncConfig.wiimAddressFlow.collectLatest {
                wiimIp = it
                Log.i(TAG, "ip set $wiimIp")
            }
            volumeSyncConfig.maxVolumeFlow.collectLatest {
                maxVol = it
                Log.i(TAG, "maxVol set $maxVol")
            }
        }

        val thread = HandlerThread("VolumeSyncThread", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        serviceLooper = thread.looper
        serviceHandler = ServiceHandler(thread.looper)

        settingsObserver = SettingsContentObserver(
            context = this,
            handler = serviceHandler!!
        )

        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            settingsObserver!!
        )

        Log.i(TAG, "Observer registered")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "Starting Service")

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

        super.onDestroy()


        Log.i(TAG, "Service Destroyed")
    }
}