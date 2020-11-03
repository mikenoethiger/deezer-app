package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.*
import kotlinx.coroutines.*
import java.util.*

class ArtistModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val topTracksLimit = 5
    private val tracksToLoad = 30
    private var modelHistory: Stack<ModelState> = Stack()

    private var currentArtist: Artist by mutableStateOf(NULL_ARTIST)
    var isLoading by mutableStateOf(false)
    var trackList: List<Track> by mutableStateOf(emptyList())
    var albums: List<Album> by mutableStateOf(emptyList())
    var contributors: List<Artist> by mutableStateOf(emptyList())

    fun getArtist(): Artist {
        return currentArtist
    }

    fun setArtist(artist: Artist) {
        currentArtist = artist
        contributors = emptyList()
        isLoading = true
        modelScope.launch {
            trackList = deezerService.loadTracks(artist, tracksToLoad)
            val searchTracks = deezerService.extendedSearch(mapOf(Pair("artist", currentArtist.name)))
            albums = deezerService.uniqueAlbums(searchTracks).toList()
            contributors =  deezerService.uniqueContributors(trackList).toList()
            isLoading = false
            // load artist again (some data such as nbFans might be missing)
            val fullArtist = deezerService.loadArtist(currentArtist.id)
            fullArtist.imageX120 = currentArtist.imageX120
            fullArtist.imageX400 = currentArtist.imageX400
            // setting to NULL_ARTIST first as a workaround, because we have the same ID,
            // hence the instances are considered equal and a direct assignment would not change anything
            currentArtist = NULL_ARTIST
            currentArtist = fullArtist
            // save model state to history
            modelHistory.push(ModelState(currentArtist, trackList, albums, contributors))
            // load track album images
            modelScope.launch { trackList.forEach{ deezerService.lazyLoadImages(it.album) } }
            // load album images
            modelScope.launch { albums.forEach{ deezerService.lazyLoadImages(it) } }
            // load contributor images
            modelScope.launch {  contributors.forEach{ deezerService.lazyLoadImages(it) } }
        }
    }
    fun setPreviousArtist() {
        if (modelHistory.isEmpty()) return
        // remove current artist
        modelHistory.pop()
        if (modelHistory.isEmpty()) return
        val modelState = modelHistory.peek()
        currentArtist = modelState.artist
        trackList = modelState.trackList
        albums = modelState.albums
        contributors = modelState.contributors
    }

    fun getTopTracks(): List<Track> {
        if (trackList.size < topTracksLimit) return trackList
        return trackList.subList(0, topTracksLimit-1)
    }
    fun getMoreTracks(): List<Track> {
        if (trackList.size <= topTracksLimit) return emptyList()
        return trackList.subList(topTracksLimit, trackList.lastIndex)
    }

    private data class ModelState(val artist: Artist, val trackList: List<Track>, val albums: List<Album>, val contributors: List<Artist>)
}