package org.dimalei.wiimvolumesync.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class VolumeSyncConfig(private val context: Context) {


    companion object {
        val WIIM_IP_ADDRESS_KEY = stringPreferencesKey("WIIM_IP_ADDRESS")
        val VOLUME_STEP_KEY = intPreferencesKey("VOLUME_STEP")
        val PIN_BASE_KEY = stringPreferencesKey("PIN_BASE")
    }


    suspend fun storeConfig(wiimAddress: String, volumeStep: Int, pinBase: String) {
        context.dataStore.edit {
            it[WIIM_IP_ADDRESS_KEY] = wiimAddress
            it[VOLUME_STEP_KEY] = volumeStep
            it[PIN_BASE_KEY] = pinBase
        }
    }

    suspend fun ensureDefaults() {
        context.dataStore.edit { prefs ->
            if (WIIM_IP_ADDRESS_KEY !in prefs) {
                prefs[WIIM_IP_ADDRESS_KEY] = "192.168.1.53"
            }
            if (VOLUME_STEP_KEY !in prefs) {
                prefs[VOLUME_STEP_KEY] = 2
            }
            if (PIN_BASE_KEY !in prefs) {
                prefs[PIN_BASE_KEY] = "p2NKrN70qaSrxvfXASCT+A0/iKenUHL27yU1rNmCz64="
            }
        }
    }

    val wiimAddressFlow: Flow<String> = context.dataStore.data.map {
        it[WIIM_IP_ADDRESS_KEY] ?: ""
    }

    val maxVolumeFlow: Flow<Int> = context.dataStore.data.map {
        it[VOLUME_STEP_KEY] ?: 2
    }

    val pinBaseFlow: Flow<String> = context.dataStore.data.map {
        it[PIN_BASE_KEY] ?: "p2NKrN70qaSrxvfXASCT+A0/iKenUHL27yU1rNmCz64="
    }

}