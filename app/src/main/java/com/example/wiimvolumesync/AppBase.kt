package com.example.wiimvolumesync

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.wiimvolumesync.viewmodel.VolumeSyncModel

abstract class AppBase<T : ViewModel>() {

    lateinit var viewModel: VolumeSyncModel

    /**
     * if activity / context is needed in services or state depends on it
     */
    open fun onNewActivity(activity: ComponentActivity) {
    }

    /**
     * nothing to do
     */
    open fun onStop(activity: ComponentActivity) {}

    /**
     * Das gesamte UI der App
     */
    @Composable
    abstract fun CreateUI()
}