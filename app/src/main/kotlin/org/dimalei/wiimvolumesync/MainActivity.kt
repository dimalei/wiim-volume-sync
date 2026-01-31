package org.dimalei.wiimvolumesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.VolumeSyncConfig
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

class MainActivity : ComponentActivity() {


    private lateinit var volumeSyncConfig: VolumeSyncConfig
    private lateinit var viewModel: VolumeSyncModel

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) viewModel.fetchConfig()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preference DataStore
        volumeSyncConfig = VolumeSyncConfig(this)
        CoroutineScope(Dispatchers.IO).launch {
            volumeSyncConfig.ensureDefaults()
        }


        // init viewmodel
        viewModel = VolumeSyncModel(applicationContext)
        VolumeSyncApp.attach(viewModel)
        setContent {
            VolumeSyncApp.CreateUI()
        }
    }
}

