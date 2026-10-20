package org.dimalei.wiimvolumesync

import androidx.compose.runtime.Composable
import org.dimalei.wiimvolumesync.ui.AppUI
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

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