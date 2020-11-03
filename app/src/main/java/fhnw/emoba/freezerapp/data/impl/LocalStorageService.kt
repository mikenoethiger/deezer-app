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

    private val SEPERATOR = "±"
    private val FAVORITE_TRACKS = preferencesKey<String>("favorite_tracks")
    private val SEARCH_HISTORY = preferencesKey<String>("search_history")

    override suspend fun writeFavoriteTracks(trackIDs: List<Int>) {
        dataStore.edit { settings ->
            settings[FAVORITE_TRACKS] = trackIDs.joinToString(separator = SEPERATOR)
        }
    }

    override fun readFavoriteTracks(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            when (preferences[FAVORITE_TRACKS]) {
                null -> emptyList()
                "" -> emptyList()
                else -> preferences[FAVORITE_TRACKS]!!.split(SEPERATOR).map{ strID -> strID.trim().toInt()}
            }
        }
    }

    override suspend fun writeSearchHistory(searchTerms: List<String>) {
        searchTerms.forEach{
            if (it.contains(SEPERATOR)) error("search term '$it' contains illegal character '$SEPERATOR' which is reserved as a structural symbol to store the data")
        }
        dataStore.edit { settings ->
            settings[SEARCH_HISTORY] = searchTerms.joinToString(separator = SEPERATOR) { it.trim() }
        }
    }

    override fun readSearchHistory(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            when (preferences[SEARCH_HISTORY]) {
                null -> emptyList()
                "" -> emptyList()
                else -> preferences[SEARCH_HISTORY]!!.split(SEPERATOR).map{ strID -> strID.trim()}
            }
        }
    }
}