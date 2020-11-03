package fhnw.emoba.freezerapp.model

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.NULL_TRACK
import fhnw.emoba.freezerapp.data.Track
import kotlinx.coroutines.*

object PlayerModel {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var timeSliderJob: Job
    // isPlaying is already used by MediaPlayer, therefore using another name here
    var currentlyPlaying by mutableStateOf(false)
    var currentMillis by mutableStateOf(0) // number of milliseconds playing the current track
    var isReady by mutableStateOf(false)
    private var track by mutableStateOf(NULL_TRACK)
    private var trackList: List<Track> by mutableStateOf(emptyList())
    private var trackListName by mutableStateOf("")

    private val player = MediaPlayer().apply {
        setOnCompletionListener { nextTrack() }
        setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
        setOnPreparedListener {
            isReady = true
            // if player was playing before loading the track, continue playing
            if (currentlyPlaying) start()
        }
    }

    fun trackList() = trackList
    fun track() = track
    fun trackListName() = trackListName
    fun setTrack(track: Track, trackList: List<Track>, trackListName: String) {
        loadTrack(track)
        this.track = track
        this.trackList = trackList
        this.trackListName = trackListName
    }

    fun play() {
        currentlyPlaying = true
        // player might not be ready when the track didn't finish loading
        // in that case the setOnPreparedListener() listener will start the player because we set currentlyPlaying = true
        if (isReady) player.start()
        startTimeSliderJob()
    }
    fun pause() {
        player.pause()
        currentlyPlaying = false
        stopTimeSliderJob()
    }
    fun nextTrack() {
        var idx = trackList.indexOf(track)
        if (idx == trackList.size-1) idx = -1
        loadTrack(trackList[idx+1])
        currentMillis = 0
    }
    fun previousTrack() {
        // play previous track if less than a second was played of current track
        if (player.currentPosition < 2000) {
            var idx = trackList.indexOf(track)
            // go to last song, if first song in track list is being played
            if (idx == 0) idx = trackList.size
            loadTrack(trackList[idx-1])
        } else {
            // otherwise play current track from beginning
            player.seekTo(0)
        }
        currentMillis = 0
    }

    private fun loadTrack(track: Track) {
        isReady = false
        player.reset()
        player.setDataSource(track.preview)
        player.prepareAsync()
    }

    private fun startTimeSliderJob() {
        timeSliderJob = modelScope.launch {
            while (true) {
                currentMillis = player.currentPosition
                delay(500)
            }
        }
    }
    private fun stopTimeSliderJob() {
        timeSliderJob.cancel()
    }

}