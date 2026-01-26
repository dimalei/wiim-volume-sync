package com.example.wiimvolumesync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wiimvolumesync.data.VolumeSyncConfig
import com.example.wiimvolumesync.services.VolumeSync
import com.example.wiimvolumesync.viewmodel.VolumeSyncModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var volumeSyncConfig: VolumeSyncConfig
    private lateinit var viewModel: VolumeSyncModel

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) viewModel.init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preference DataStore
        volumeSyncConfig = VolumeSyncConfig(this)
        CoroutineScope(Dispatchers.IO).launch {
            volumeSyncConfig.ensureDefaults()
        }

        // init viewmodel
        viewModel = VolumeSyncModel(volumeSyncConfig)
        VolumeSyncApp.attach(viewModel)
        setContent {
            VolumeSyncApp.CreateUI()
        }

        // start service
        startService(Intent(this, VolumeSync::class.java))

    }
}

