package fhnw.emoba.freezerapp.data

import kotlinx.coroutines.flow.Flow

interface StorageService {
    suspend fun writeFavoriteTracks(trackIDs: List<Int>)
    fun readFavoriteTracks(): Flow<List<Int>>

    suspend fun writeSearchHistory(searchTerms: List<String>)
    fun readSearchHistory(): Flow<List<String>>
}