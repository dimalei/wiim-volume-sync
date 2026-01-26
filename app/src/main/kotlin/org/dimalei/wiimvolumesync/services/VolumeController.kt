package org.dimalei.wiimvolumesync.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolumeController {
    fun getVolume(
        scope: CoroutineScope,
        ipAddress: String,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            // TODO: use connector
        }

    }

    fun setVolume(vol: Int) {

    }
}