package com.example.wiimvolumesync.data

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
        val MAX_VOLUME_KEY = intPreferencesKey("MAX_VOLUME")
    }


    suspend fun storeConfig(wiimAddress: String, maxVol: Int) {
        context.dataStore.edit {
            it[WIIM_IP_ADDRESS_KEY] = wiimAddress
            it[MAX_VOLUME_KEY] = maxVol
        }
    }

    suspend fun ensureDefaults() {
        context.dataStore.edit { prefs ->
            if (WIIM_IP_ADDRESS_KEY !in prefs) {
                prefs[WIIM_IP_ADDRESS_KEY] = "192.168.1.53"
            }
            if (MAX_VOLUME_KEY !in prefs) {
                prefs[MAX_VOLUME_KEY] = 50
            }
        }
    }

    val wiimAddressFlow: Flow<String> = context.dataStore.data.map {
        it[WIIM_IP_ADDRESS_KEY] ?: ""
    }

    val maxVolumeFlow: Flow<Int> = context.dataStore.data.map {
        it[MAX_VOLUME_KEY] ?: 50
    }

}