package org.dimalei.wiimvolumesync.activity.viewmodel

import android.net.InetAddresses
import android.util.Log
import android.webkit.URLUtil
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.settings.AppSettings
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.dimalei.wiimvolumesync.data.wiim.WiimConnector
import org.dimalei.wiimvolumesync.data.wiim.getServerPublicKeyPin


const val MAX_VOL_STEP = 10
const val MIN_VOL_STEP = 1

class VolumeSyncModel(
    val settingsRepository: SettingsRepository,
    val wiimConnector: WiimConnector
) : ViewModel() {

    val tag = this.javaClass.simpleName

    var logs = mutableStateListOf<String>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // input
    var wiimIpAddress by mutableStateOf("")
    var volumeStep by mutableStateOf("2")
    lateinit var pubKeyHash: String
    var netflixButtonUrl by mutableStateOf("")

    val openEditDialog = mutableStateOf(false)


    init {
        scope.launch {
            settingsRepository.settingsFlow.collectLatest {
                wiimIpAddress = it.wiimIpAddress
                volumeStep = "${it.volumeStep}"
                pubKeyHash = it.pubKeyHash
                netflixButtonUrl = it.netflixButtonUrl
                Log.d(
                    tag,
                    "settings updated wiimIpAddress: $wiimIpAddress, volumeStep: $volumeStep, pubKeyHash: $pubKeyHash, netflixButtonUrl: $netflixButtonUrl"
                )
            }
        }
    }

    val ipHasErrors by derivedStateOf {
        !InetAddresses.isNumericAddress(wiimIpAddress)
    }
    val volumeStepHasErrors by derivedStateOf {
        !(volumeStep.isNotBlank() && volumeStep.isDigitsOnly() && Integer.valueOf(volumeStep.trim()) in MIN_VOL_STEP..MAX_VOL_STEP)
    }
    val volumeStepErrorMessage = "Must be $MIN_VOL_STEP - $MAX_VOL_STEP"

    val netflixActionUrlHasErrors by derivedStateOf {
        !URLUtil.isValidUrl(netflixButtonUrl) && netflixButtonUrl.isNotEmpty()
    }

    val netflixActionUrlErrorMessage = "Must be a valid URL"

    fun logMessage(message: String) {
        if (logs.size > 100) {
            logs.removeAt(0)
        }
        logs.add(message)
    }

    fun verifyAndApply() {
        if (ipHasErrors || volumeStepHasErrors) {
            logMessage("Your input has errors.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getPublicKeyPin()
                apply()
                test()
                logMessage("Success ✔\uFE0F")
            } catch (e: Exception) {
                logMessage(e.message ?: "Unknown error: ${e.toString()}")
            }
        }
    }

    private fun getPublicKeyPin() {
        getServerPublicKeyPin(
            host = wiimIpAddress,
            onSuccess = {
                pubKeyHash = it
                logMessage("SSLKeyHash extracted: $it")
            },
            onError = { throw it }
        )
    }

    private suspend fun apply() {
        settingsRepository.storeSettings(
            AppSettings(
                wiimIpAddress = wiimIpAddress,
                volumeStep = Integer.valueOf(volumeStep),
                pubKeyHash = pubKeyHash,
                netflixButtonUrl = netflixButtonUrl
            )
        )
    }

    private fun test() {
        wiimConnector.getDeviceStatus(
            onSuccess = { logMessage("Device Found: " + it.getString("DeviceName")) },
            onError = { throw it }
        )
    }

    fun testManually() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                test()
            } catch (e: Exception) {
                logMessage(e.message ?: "Unknown error: ${e}")
            }
        }
    }
}