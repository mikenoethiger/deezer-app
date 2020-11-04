package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.NULL_ALBUM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlbumModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var isLoading by mutableStateOf(false)
    var album by mutableStateOf(NULL_ALBUM)

    fun loadAlbum(albumId: Int) {
        isLoading = true
        modelScope.launch {
            album = deezerService.loadAlbum(albumId)
            isLoading = false
            // load album images
            modelScope.launch {
                deezerService.lazyLoadImages(album)
                // set track album images
                album.tracks.forEach{ track ->
                    track.album.imageX120 = album.imageX120
                    track.album.imageX400 = album.imageX400
                }
            }
            // load artist images
            modelScope.launch { deezerService.lazyLoadImages(album.artist) }
        }
    }
}