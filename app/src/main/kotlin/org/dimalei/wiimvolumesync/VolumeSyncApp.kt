package org.dimalei.wiimvolumesync

import androidx.compose.runtime.Composable
import org.dimalei.wiimvolumesync.activity.viewmodel.VolumeSyncModel
import org.dimalei.wiimvolumesync.ui.AppUI

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