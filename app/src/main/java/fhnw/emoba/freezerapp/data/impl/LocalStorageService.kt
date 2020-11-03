package fhnw.emoba.freezerapp.data.impl

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import fhnw.emoba.freezerapp.FreezerApp
import fhnw.emoba.freezerapp.data.StorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalStorageService(private val dataStore: DataStore<Preferences>) : StorageService {

    private val FAVORITE_TRACKS = preferencesKey<String>("favorite_tracks")

    override suspend fun writeFavoriteTracks(trackIDs: List<Int>) {
        dataStore.edit { settings ->
            settings[FAVORITE_TRACKS] = trackIDs.joinToString(separator = ",")
        }
    }

    override fun readFavoriteTracks(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            when (preferences[FAVORITE_TRACKS]) {
                null -> emptyList()
                "" -> emptyList()
                else -> preferences[FAVORITE_TRACKS]!!.split(",").map{ strID -> strID.trim().toInt()}
            }
        }
    }
}