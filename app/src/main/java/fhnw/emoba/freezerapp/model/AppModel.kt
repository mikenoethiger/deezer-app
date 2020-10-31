package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.freezerapp.data.DeezerService
import fhnw.emoba.freezerapp.data.ImageSize
import fhnw.emoba.freezerapp.data.Track
import kotlinx.coroutines.*

class AppModel(private val deezerService: DeezerService) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val appTitle = "Freezer App"
    var isPlayerOpen by mutableStateOf(false)

    var selectedTab: MainMenu by mutableStateOf(MainMenu.SEARCH)

    var isLoading by mutableStateOf(false)

    var songsSearchText by mutableStateOf("")
    var searchTrackList: List<Track> by mutableStateOf(emptyList())
    var searchArtists: List<Track.Artist> by mutableStateOf(emptyList())
    var searchAlbums: List<Track.Album> by mutableStateOf(emptyList())

    var screenStack: List<Screen> by mutableStateOf(emptyList())

    fun getOpenSubScreen(mainMenu: MainMenu): (@Composable () -> Unit)? {
        screenStack.reversed().forEach {
            if (it.visible) return it.composeFunction
        }
        return null
    }
    fun getClosedSubScreen(mainMenu: MainMenu): (@Composable () -> Unit)? {
        screenStack.forEach {
            if (!it.visible) return it.composeFunction
        }
        return null
    }
    fun openScreen(screen: @Composable () -> Unit) {
        screenStack = screenStack.plus(Screen(screen, true))
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
            loadCovers(searchTrackList)
        }
    }

    private fun loadCovers(tracks: List<Track>) {
        tracks.forEach {
            modelScope.launch {
                it.album.coverX400 = deezerService.getAlbumCover(it.album.id, ImageSize.x400)
                it.album.coverX120 = deezerService.getAlbumCover(it.album.id, ImageSize.x120)
                // it.artist.pictureX120 = deezerService.getArtistCover(it.artist.id, ImageSize.x120)
                it.artist.pictureX400 = deezerService.getArtistCover(it.artist.id, ImageSize.x400)
//                it.album.coverX1000 = deezerService.getAlbumCover(it.album.id, ImageSize.x1000)
            }
        }
    }
}

class Screen(val composeFunction: @Composable () -> Unit, var visible: Boolean)
class ScreenTransition(val newScreen: () -> Unit, val oldScreen: (() -> Unit) ? = null, val forward: Boolean = true)