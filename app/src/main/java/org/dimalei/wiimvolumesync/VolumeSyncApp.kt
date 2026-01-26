package org.dimalei.wiimvolumesync

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import org.dimalei.wiimvolumesync.ui.AppUI
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

object VolumeSyncApp : AppBase<VolumeSyncModel>() {

    fun attach(viewModel: VolumeSyncModel) {
        this.viewModel = viewModel
    }

    override fun onNewActivity(activity: ComponentActivity) {
        viewModel.init()
    }

    @Composable
    override fun CreateUI() {
        AppUI(viewModel)
    }
}