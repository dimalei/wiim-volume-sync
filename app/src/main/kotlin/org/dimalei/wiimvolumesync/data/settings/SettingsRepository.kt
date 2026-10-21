package org.dimalei.wiimvolumesync.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.dimalei.wiimvolumesync.data.settings.GuiLogger.log


private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {

    val tag: String = javaClass.simpleName
    private val dataStore = context.applicationContext.dataStore

    companion object PreferenceKeys {
        val WIIM_IP_ADDRESS_KEY = stringPreferencesKey("WIIM_IP_ADDRESS")
        val VOLUME_STEP_KEY = intPreferencesKey("VOLUME_STEP")
        val PUB_KEY_HASH_KEY = stringPreferencesKey("PUB_KEY_HASH_KEY")
        val NETFLIX_BUTTON_URL = stringPreferencesKey("NETFLIX_BUTTON_URL")
    }

    suspend fun storeSettings(settings: AppSettings) {
        dataStore.edit {
            it[WIIM_IP_ADDRESS_KEY] = settings.wiimIpAddress
            it[VOLUME_STEP_KEY] = settings.volumeStep
            it[PUB_KEY_HASH_KEY] = settings.pubKeyHash
            it[NETFLIX_BUTTON_URL] = settings.netflixButtonUrl
        }
        log(tag, "settings stored: $settings")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data
        .map {
            AppSettings(
                wiimIpAddress = it[WIIM_IP_ADDRESS_KEY] ?: "",
                volumeStep = it[VOLUME_STEP_KEY] ?: 2,
                pubKeyHash = it[PUB_KEY_HASH_KEY] ?: "",
                netflixButtonUrl = it[NETFLIX_BUTTON_URL] ?: ""
            )
        }
}