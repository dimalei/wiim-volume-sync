package org.dimalei.wiimvolumesync.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.dimalei.wiimvolumesync.VolumeSyncApp
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.dimalei.wiimvolumesync.data.wiim.WiimConnector
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: VolumeSyncModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepository = SettingsRepository(applicationContext)
        val wiimConnector = WiimConnector(settingsRepository)
        viewModel = VolumeSyncModel(settingsRepository, wiimConnector)
        VolumeSyncApp.attach(viewModel)
        setContent {
            VolumeSyncApp.CreateUI()
        }
    }
}

