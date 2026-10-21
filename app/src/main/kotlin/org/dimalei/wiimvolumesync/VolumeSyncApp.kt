package org.dimalei.wiimvolumesync

import androidx.compose.runtime.Composable
import org.dimalei.wiimvolumesync.activity.ui.AppUI
import org.dimalei.wiimvolumesync.activity.viewmodel.VolumeSyncModel

object VolumeSyncApp {

    lateinit var viewModel: VolumeSyncModel
    fun attach(viewModel: VolumeSyncModel) {
        this.viewModel = viewModel
    }

    @Composable
    fun CreateUI() {
        AppUI(viewModel)
    }
}