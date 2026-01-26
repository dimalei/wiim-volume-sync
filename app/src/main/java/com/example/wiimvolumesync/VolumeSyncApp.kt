package com.example.wiimvolumesync

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.example.wiimvolumesync.ui.AppUI
import com.example.wiimvolumesync.viewmodel.VolumeSyncModel

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