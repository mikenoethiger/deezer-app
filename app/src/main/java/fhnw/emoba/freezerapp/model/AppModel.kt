package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.Track
import kotlinx.coroutines.*

class AppModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val appTitle = "Freezer App"
    var isPlayerOpen by mutableStateOf(false)

    var currentMenu: MainMenu by mutableStateOf(MainMenu.SEARCH)

    var isLoading by mutableStateOf(false)

    var songsSearchText by mutableStateOf("")
    var searchTrackList: List<Track> by mutableStateOf(emptyList())
    var searchArtists: List<Track.Artist> by mutableStateOf(emptyList())
    var searchAlbums: List<Track.Album> by mutableStateOf(emptyList())

    private var nestedScreens: MutableMap<MainMenu, List<Screen>> = mutableMapOf()
    var currentNestedScreen: Map<MainMenu, Screen> by mutableStateOf(mapOf())

    fun getCurrentNestedScreen(defaultUI: @Composable () -> Unit): Screen {
        if (!currentNestedScreen.containsKey(currentMenu)) return Screen(defaultUI, true)
        return currentNestedScreen[currentMenu]!!
    }
    fun closeNestedScreen() {
        if (!nestedScreens.containsKey(currentMenu)) return
        if (nestedScreens[currentMenu]!!.isEmpty()) return
        if (nestedScreens[currentMenu]!!.size == 1) {
            nestedScreens[currentMenu] = emptyList()
            currentNestedScreen = currentNestedScreen.minus(currentMenu)
        } else {
            nestedScreens[currentMenu]!!.dropLast(1)
            val newLast = nestedScreens[currentMenu]!!.last().copy(isOpeningTransition = false)
            currentNestedScreen = currentNestedScreen.plus(Pair(currentMenu, newLast))
        }
    }
    fun openNestedScreen(ui: @Composable () -> Unit) {
        nestedScreens.putIfAbsent(currentMenu, emptyList())
        val screen = Screen(ui, true)
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
            deezerService.loadAlbumCoversAsync(searchTrackList)
            deezerService.loadArtistCoversAsync(searchTrackList)
        }
    }

    fun createArtistModel(artist: Track.Artist): ArtistModel {
        println("creating artist model")
        return ArtistModel(deezerService, artist)
    }

}

// if isOpeningTransition is true, then the transition is meant to open the screen, otherwise close the screen
// might be relevant for animation, for example closing transitions may want to slide in the visible screen from left to right
// while opening transition may want to slide in the visible screen from right to left
data class Screen(val composeFunction: @Composable () -> Unit, val isOpeningTransition: Boolean)
class ScreenTransition(val newScreen: () -> Unit, val oldScreen: (() -> Unit) ? = null, val forward: Boolean = true)