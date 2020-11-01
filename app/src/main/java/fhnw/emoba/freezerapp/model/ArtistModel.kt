package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.mutableStateOf
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.Track
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import java.lang.IllegalStateException

class ArtistModel(private val deezerService: DeezerService, val artist: Track.Artist) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var top5Tracks: List<Track> by mutableStateOf(emptyList())
    var top5TracksLoaded by mutableStateOf(false)
    var moreTracks: List<Track> by mutableStateOf(emptyList())
    var moreTracksLoaded by mutableStateOf(false)
    var artistFans by mutableStateOf(artist.nbFan)

    init {
        println("running init")
        loadArtistFans()
        loadTop5Tracks()
        loadMoreTracks()
    }

    private fun loadArtistFans() {
        // it is possible that the Track.Artist instance passed in the constructor does not contain the nbFans attribute,
        // some endpoints such as search return a list of tracks with the artist for each track, but not all artist data
        // is given, such as for instance the nbFans
        if (artistFans == 0) {
            modelScope.launch {
                waitUntilMutableStateReadable()
                artistFans = deezerService.loadArtist(artist.id).nbFan
            }
        }
    }

    private fun loadTop5Tracks() {
        if (top5TracksLoaded) return
        modelScope.launch {
            waitUntilMutableStateReadable()
            top5Tracks = deezerService.loadTopTracks(artist, limit=5, index=0)
            top5TracksLoaded = true
            deezerService.loadAlbumCoversAsync(top5Tracks)
        }
    }

    private fun loadMoreTracks() {
        if (moreTracksLoaded) return
        modelScope.launch {
            waitUntilMutableStateReadable()
            moreTracks = deezerService.loadTopTracks(artist, limit=30, index=5)
            moreTracksLoaded = true
            deezerService.loadAlbumCoversAsync(moreTracks)
        }
    }

    private suspend fun waitUntilMutableStateReadable() {
        // Experience showed, that when accessing a mutableStateOf() variable from a coroutine,
        // short after the object has been created, the following IllegalStateException is thrown:
        // "Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied"
        // No better solution than the one presented in this function could be found yet.
        // In this function we try to access a mutable variable as long as the IllegalStateException is thrown.
        var ready = false
        while (!ready) {
            try {
                artistFans = artistFans
                ready = true
            } catch (e: IllegalStateException) {
                // experiments showed, that a wait of 10ms makes the while loop iterate only twice
                // without the delay, there were ~550 iterations
                delay(10)
            }
        }
    }
}