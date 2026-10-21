package org.dimalei.wiimvolumesync.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.dimalei.wiimvolumesync.VolumeSyncApp
import org.dimalei.wiimvolumesync.activity.viewmodel.VolumeSyncModel
import org.dimalei.wiimvolumesync.data.settings.GuiLogger
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.dimalei.wiimvolumesync.data.wiim.WiimConnector

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: VolumeSyncModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepository = SettingsRepository(applicationContext)
        val wiimConnector = WiimConnector(settingsRepository)
        viewModel = VolumeSyncModel(settingsRepository, wiimConnector)
        GuiLogger.registerLoggerOut(viewModel::logMessage)
        VolumeSyncApp.attach(viewModel)
        setContent {
            VolumeSyncApp.CreateUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GuiLogger.removeLoggerOut(viewModel::logMessage)
    }
}

