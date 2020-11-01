package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.mutableStateOf
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.Track
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.NULL_ARTIST
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CountDownLatch

class ArtistModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var artistHistory: Stack<Track.Artist> = Stack()

    private var currentArtist: Track.Artist by mutableStateOf(NULL_ARTIST)
    var top5Tracks: List<Track> by mutableStateOf(emptyList())
    var top5TracksLoading by mutableStateOf(false)
    var moreTracks: List<Track> by mutableStateOf(emptyList())
    var moreTracksLoading by mutableStateOf(false)
    var albums: List<Track.Album> by mutableStateOf(emptyList())
    var albumsLoading by mutableStateOf(false)
    var contributors: List<Track.Artist> by mutableStateOf(emptyList())

    fun getArtist(): Track.Artist {
        return currentArtist
    }
    fun setArtist(artist: Track.Artist) {
        artistHistory.push(artist)
        currentArtist = artist
        contributors = emptyList()
        loadNbFanAsync()
        loadTop5TracksAsync()
        loadMoreTracksAsync()
        loadAlbumsAsync()
    }
    fun setPreviousArtist() {
        if (artistHistory.isEmpty()) return
        currentArtist = artistHistory.pop()
    }

    private fun loadNbFanAsync() {
        // It is possible that the currentArtist instance does not contain all artist attributes,
        // some deezer API endpoints such as the search return a list of tracks with the artist for each track,
        // but not all artist attributes are present, such as for instance the nbFans
        if (currentArtist.nbFan == 0) {
            modelScope.launch {
                val artist = deezerService.loadArtist(currentArtist.id)
                currentArtist.nbFan = artist.nbFan
            }
        }
    }

    private fun loadTop5TracksAsync() {
        top5TracksLoading = true
        modelScope.launch {
            top5Tracks = deezerService.loadTopTracks(currentArtist, limit=5, index=0)
            top5TracksLoading = false
            refreshContributors()
            deezerService.loadAlbumCoversAsync(top5Tracks)
            deezerService.loadArtistCoversAsync(top5Tracks)
        }
    }

    private fun loadMoreTracksAsync() {
        moreTracksLoading = true
        modelScope.launch {
            moreTracks = deezerService.loadTopTracks(currentArtist, limit=30, index=5)
            moreTracksLoading = false
            refreshContributors()
            deezerService.loadAlbumCoversAsync(moreTracks)
            deezerService.loadArtistCoversAsync(moreTracks)
        }
    }

    private fun loadAlbumsAsync() {
        albumsLoading = true
        modelScope.launch {
            val tracks = deezerService.extendedSearch(mapOf(Pair("artist", currentArtist.name)))
            albums = deezerService.uniqueAlbums(tracks).toList()
            albumsLoading = false
            deezerService.loadAlbumCoversAsync2(albums)
        }
    }

    private fun refreshContributors() {
        contributors =  deezerService.uniqueContributors(top5Tracks.plus(moreTracks)).toList()
    }
}