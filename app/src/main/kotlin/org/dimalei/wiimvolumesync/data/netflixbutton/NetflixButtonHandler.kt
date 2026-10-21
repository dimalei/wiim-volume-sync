package org.dimalei.wiimvolumesync.data.netflixbutton

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.dimalei.wiimvolumesync.data.settings.GuiLogger.log
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository

class NetflixButtonHandler(settingsRepository: SettingsRepository) {

    val tag: String = javaClass.simpleName
    val client = SimpleHttpRequester
    var url: String = ""

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    init {
        scope.launch {
            settingsRepository.settingsFlow.collectLatest {
                url = it.netflixButtonUrl
            }
        }
    }

    fun handle() {
        if (url.isNotEmpty()) {
            log(tag, "handle button press ...")
            client.send(url)
        } else {
            log(tag, "no url defined.")
        }
    }
}