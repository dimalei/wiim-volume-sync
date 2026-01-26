package org.dimalei.wiimvolumesync.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.getPlayerStatus as getWiimStatus
import org.dimalei.wiimvolumesync.data.setPlayerVolume as setWiimVolume

class VolumeController {
    fun getVolume(
        ipAddress: String,
        expectedPinBase64: String,
        scope: CoroutineScope,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            getWiimStatus(
                ipAddress = ipAddress,
                expectedPinBase64 = expectedPinBase64,
                onSuccess = {
                    onSuccess(it.getInt("vol"))
                },
                onError = onError
            )
        }

    }

    fun setVolume(
        vol: Int,
        ipAddress: String,
        expectedPinBase64: String,
        scope: CoroutineScope,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            setWiimVolume(
                ipAddress = ipAddress,
                expectedPinBase64 = expectedPinBase64,
                volume = vol,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}