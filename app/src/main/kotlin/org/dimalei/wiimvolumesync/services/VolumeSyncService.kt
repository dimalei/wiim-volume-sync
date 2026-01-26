package org.dimalei.wiimvolumesync.services

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig
import org.dimalei.wiimvolumesync.data.setPlayerVolume

class VolumeSyncService : Service() {
    // logging
    val tag: String = this.javaClass.simpleName

    // service overhead
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    // config
    private lateinit var volumeSyncConfig: VolumeSyncConfig
    private var configJob: Job? = null

    var wiimIp: String? = null
    var maxVol: Int = 0
    var pinBase: String? = null

    // audio manager
    private lateinit var audioManager: AudioManager
    private var lastVol: Int = -1


    override fun onCreate() {

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // auto update config
        fetchConfig()

        val thread = HandlerThread(
            "VolumeSyncThread",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        thread.start()

        serviceScope.launch {
            observeVolumeButtonsByPolling()
        }

        Log.d(tag, "Volume Sync Service created")
    }

    private fun fetchConfig() {
        volumeSyncConfig = VolumeSyncConfig(applicationContext)
        serviceScope.launch {
            volumeSyncConfig.wiimAddressFlow.collectLatest {
                wiimIp = it
                Log.d(tag, "ip updated $wiimIp")
            }
        }
        serviceScope.launch {
            volumeSyncConfig.maxVolumeFlow.collectLatest {
                maxVol = it
                Log.d(tag, "maxVol updated $maxVol")
            }
        }
        serviceScope.launch {
            volumeSyncConfig.pinBaseFlow.collectLatest {
                pinBase = it
                Log.d(tag, "pinBase updated $pinBase")
            }
        }
    }

    private suspend fun observeVolumeButtonsByPolling() {
        // Choose the stream you care about (usually MUSIC)
        val stream = AudioManager.STREAM_MUSIC

        while (currentCoroutineContext().isActive) {
            val current = audioManager.getStreamVolume(stream)
            if (current != lastVol) {
                lastVol = current
                changeVolume(current) // current is already 0..maxStreamVolume
            }
            delay(150) // 100-250ms is a common range
        }
    }

    private fun changeVolume(streamVol: Int) {
        // Convert stream volume scale to your WiiM scale
        val streamMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val finalVol = (streamVol * maxVol) / streamMax

        Log.d(tag, "Setting volume to $finalVol ...")

        if (wiimIp == null || pinBase == null) {
            Log.d(tag, "config is still null")
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            setPlayerVolume(
                ipAddress = wiimIp!!,
                expectedPinBase64 = pinBase!!,
                volume = finalVol,
                onSuccess = { Log.d(tag, "Success! response: $it") },
                onError = { Log.d(tag, it.toString()) }
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        Log.i(tag, "Service Destroyed")
    }

}