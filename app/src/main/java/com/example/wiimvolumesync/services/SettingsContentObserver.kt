package com.example.wiimvolumesync.services

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.util.Log


class SettingsContentObserver(val context: Context, handler: Handler) : ContentObserver(handler) {
    val TAG = javaClass.simpleName

    val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager;

    override fun deliverSelfNotifications(): Boolean {
        return false
    }

    override fun onChange(selfChange: Boolean) {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        Log.d(TAG, "system volume $currentVolume")
    }
}