package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.ImageSize
import fhnw.emoba.freezerapp.data.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FreezerModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var title = "Freezer App"
    var selectedTab: Tab by mutableStateOf(Tab.SONGS)

    var isLoading by mutableStateOf(false)

    var songsSearchText by mutableStateOf("")
    var songSearchResults: List<SearchResult> by mutableStateOf(emptyList())

    fun searchSongs() {
        if (songsSearchText.length < 2) return
        isLoading = true
        songSearchResults = emptyList()
        modelScope.launch {
            songSearchResults = deezerService.search(songsSearchText)
            isLoading = false
            loadAlbumCovers(songSearchResults)
        }
    }

    private fun loadAlbumCovers(searchResults: List<SearchResult>) {
        searchResults.forEach {
            modelScope.launch {
                it.album.cover = deezerService.getAlbumCover(it.album.id, ImageSize.x120)
            }
        }
    }
    enum class Tab(val text: String) {
        HITS("Hits"),
        SONGS("Songs"),
        ALBUMS("Albums"),
        RADIO("Radio")
    }
}