package fhnw.emoba.freezerapp.model

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.lang.IllegalStateException

class AppModel(private val deezerService: DeezerService, private val storageService: StorageService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val appTitle = "Freezer App"
    var isPlayerOpen by mutableStateOf(false)

    var isLoading by mutableStateOf(false)

    var songsSearchText by mutableStateOf("")
    var searchTrackList: List<Track> by mutableStateOf(emptyList())
    var searchArtists: List<Artist> by mutableStateOf(emptyList())
    var searchAlbums: List<Album> by mutableStateOf(emptyList())

    var favoriteTracksLoading by mutableStateOf(false)
    var favoriteTracks: List<Track> by mutableStateOf(emptyList())

    var radiosLoading by mutableStateOf(false)
    var radios: List<Radio> by mutableStateOf(emptyList())
    var currentRadio by mutableStateOf(NULL_RADIO)
    var currentRadioLoading by mutableStateOf(false)
    var currentRadioTracks: List<Track> by mutableStateOf(emptyList())
    init { loadFavoriteTracksFromLocalStorage() }

    fun lazyLoadRadios() {
        if (radios.isNotEmpty()) return
        radiosLoading = true
        modelScope.launch {
            radios = deezerService.loadRadios()
            radiosLoading = false
        }
    }
    fun setCurrentRadioAndLoadTrackList(radio: Radio) {
        currentRadioLoading = true
        currentRadio = radio
        currentRadioTracks = emptyList()
        modelScope.launch {
            currentRadioTracks = deezerService.loadTracks(currentRadio)
            currentRadioLoading = false
            // load track album images
            modelScope.launch{
                currentRadioTracks.forEach { deezerService.lazyLoadImages(it.album) }
            }
        }
    }

    fun lazyLoadImages(obj: HasImage) = modelScope.launch { deezerService.lazyLoadImages(obj) }

    private fun loadFavoriteTracksFromLocalStorage() {
        favoriteTracksLoading = true
        modelScope.launch {
            val trackIdFlow = storageService.readFavoriteTracks()
            trackIdFlow.collect { trackIDs ->
                waitUntilMutableStateReadable()
                Log.d("favorite", "loaded ${trackIDs.size} track IDs from storage")
                favoriteTracks = trackIDs.map { id ->
                    Log.d("favorite", "loading track $id")
                    deezerService.loadTrack(id)
                }
                Log.d("favorite", "loaded ${favoriteTracks.size} favorite tracks")
                favoriteTracksLoading = false
                // load album images
                favoriteTracks.forEach{ track ->
                    modelScope.launch { deezerService.lazyLoadImages(track.album) }
                }
            }
        }
    }
    fun likeTrack(track: Track) {
        favoriteTracks = favoriteTracks.plus(track)
        modelScope.launch {
            storageService.writeFavoriteTracks(favoriteTracks.map { it.id })
        }
    }
    fun unlikeTrack(track: Track) {
        favoriteTracks = favoriteTracks.minus(track)
        modelScope.launch {
            storageService.writeFavoriteTracks(favoriteTracks.map { it.id })
        }
    }
    fun isFavorite(trackID: Int): Boolean = favoriteTracks.map { it.id }.contains(trackID)

    private var nestedScreens: MutableMap<MainMenu, List<Screen>> = mutableMapOf()
    var currentNestedScreen: Map<MainMenu, Screen> by mutableStateOf(mapOf())
    private var currentMenu: MainMenu by mutableStateOf(MainMenu.SEARCH)

//    fun currentMenu() = currentMenu
//    fun setMenu(mainMenu: MainMenu) {
//        if (mainMenu == currentMenu) {
//
//        }
//    }
    fun getCurrentNestedScreen(defaultUI: @Composable () -> Unit): Screen {
        if (!currentNestedScreen.containsKey(currentMenu)) return Screen(defaultUI, true, "")
        return currentNestedScreen[currentMenu]!!
    }
    fun closeNestedScreen() {
        if (!nestedScreens.containsKey(currentMenu)) return
        if (nestedScreens[currentMenu]!!.isEmpty()) return
        if (nestedScreens[currentMenu]!!.size == 1) {
            nestedScreens[currentMenu] = emptyList()
            currentNestedScreen = currentNestedScreen.minus(currentMenu)
        } else {
            nestedScreens[currentMenu] = nestedScreens[currentMenu]!!.dropLast(1)
            val newLast = nestedScreens[currentMenu]!!.last().copy(isOpeningTransition = false)
            currentNestedScreen = currentNestedScreen.plus(Pair(currentMenu, newLast))
        }
    }
    fun openNestedScreen(previousScreenName: String, ui: @Composable () -> Unit) {
        nestedScreens.putIfAbsent(currentMenu, emptyList())
        val screen = Screen(ui, true, previousScreenName)
        nestedScreens[currentMenu] = nestedScreens[currentMenu]!!.plus(screen)
        currentNestedScreen = currentNestedScreen.plus(Pair(currentMenu, screen))
    }

    fun searchSongs() {
        if (songsSearchText.length < 2) return
        isLoading = true
        searchTrackList = emptyList()
        modelScope.launch {
            searchTrackList = deezerService.search(songsSearchText)
            searchArtists = deezerService.uniqueArtists(searchTrackList).toList()
            searchAlbums = deezerService.uniqueAlbums(searchTrackList).toList()
            isLoading = false
            searchTrackList.forEach{
                deezerService.lazyLoadImages(it.album)
                deezerService.lazyLoadImages(it.artist)
            }
        }
    }

    private suspend fun waitUntilMutableStateReadable() {
        // The following IllegalStateException is thrown if mutableStateOf variables are accessed
        // from a coroutine shortly after the object has been initialized:
        // "Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied"
        // The proper solution for this problem could not be found yet.
        // In this function we just loop as long as the IllegalStateException is thrown.
        var readable = false
        while (!readable) {
            try {
                favoriteTracksLoading = favoriteTracksLoading
                Log.d("favorite", "mutable state readable")
                readable = true
            } catch (e: IllegalStateException) {
                // Experiments showed that with a delay of 10ms, the loop is only called a few times
                // while with no delay it is called up to 500 times which is wasted CPU time
                delay(10)
            }
        }
    }
}

// if isOpeningTransition is true, then the transition is meant to open the screen, otherwise close the screen
// might be relevant for animation, for example closing transitions may want to slide in the visible screen from left to right
// while opening transition may want to slide in the visible screen from right to left
data class Screen(val composeFunction: @Composable () -> Unit, val isOpeningTransition: Boolean, val previousScreenName: String)
class ScreenTransition(val newScreen: () -> Unit, val oldScreen: (() -> Unit) ? = null, val forward: Boolean = true)