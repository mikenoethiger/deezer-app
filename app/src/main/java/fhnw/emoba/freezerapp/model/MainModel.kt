package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.ImageSize
import fhnw.emoba.freezerapp.data.Track
import kotlinx.coroutines.*

class MainModel(private val deezerService: DeezerService, val player: PlayerModel) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var selectedTab: HomeTab by mutableStateOf(HomeTab.SONGS)

    var isLoading by mutableStateOf(false)

    var songsSearchText by mutableStateOf("")
    var searchTrackList: List<Track> by mutableStateOf(emptyList())

    fun searchSongs() {
        if (songsSearchText.length < 2) return
        isLoading = true
        searchTrackList = emptyList()
        modelScope.launch {
            searchTrackList = deezerService.search(songsSearchText)
            isLoading = false
            loadAlbumCovers(searchTrackList)
        }
    }

    private fun loadAlbumCovers(tracks: List<Track>) {
        tracks.forEach {
            if (!it.album.coversLoaded) {
                modelScope.launch {
                    it.album.coverX120 = deezerService.getAlbumCover(it.album.id, ImageSize.x120)
                    it.album.coverX400 = deezerService.getAlbumCover(it.album.id, ImageSize.x400)
                    it.album.coverX1000 = deezerService.getAlbumCover(it.album.id, ImageSize.x1000)
                    it.album.coversLoaded = true
                }
            }
        }
    }

//    private lateinit var timeSliderJob: Job
//    var currentTrack by mutableStateOf(NULL_TRACK)
//    // isPlaying is already used by MediaPlayer, therefore using another name here
//    var currentlyPlaying by mutableStateOf(false)
//    var currentMillis by mutableStateOf(0) // number of milliseconds playing the current track
//    var isReady by mutableStateOf(false)
//
//    private val player = MediaPlayer().apply {
//        setOnCompletionListener { nextTrack() }
//        setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
//        setOnPreparedListener {
//            isReady = true
//            // if player was playing before loading the track, continue playing
//            if (currentlyPlaying) start()
//        }
//    }
//
//    fun loadTrack(track: Track) {
//        isReady = false
//        player.reset()
//        player.setDataSource(track.preview)
//        player.prepareAsync()
//        currentTrack = track
//    }
//    fun play() {
//        player.start()
//        currentlyPlaying = true
//        startTimeSliderJob()
//    }
//    fun pause() {
//        player.pause()
//        currentlyPlaying = false
//        stopTimeSliderJob()
//    }
//    fun nextTrack() {
//        var idx = trackList.indexOf(currentTrack)
//        if (idx == trackList.size-1) idx = -1
//        loadTrack(trackList[idx+1])
//        currentMillis = 0
//    }
//    fun previousTrack() {
//        // play previous track if less than a second was played of current track
//        if (player.currentPosition < 2000) {
//            var idx = trackList.indexOf(currentTrack)
//            // go to last song, if first song in track list is being played
//            if (idx == 0) idx = trackList.size
//            loadTrack(trackList[idx-1])
//        } else {
//            // otherwise play current track from beginning
//            player.seekTo(0)
//        }
//        currentMillis = 0
//    }
//
//    private fun startTimeSliderJob() {
//        timeSliderJob = modelScope.launch {
//            while (true) {
//                currentMillis = player.currentPosition
//                delay(500)
//            }
//        }
//    }
//    private fun stopTimeSliderJob() {
//        timeSliderJob.cancel()
//    }
}