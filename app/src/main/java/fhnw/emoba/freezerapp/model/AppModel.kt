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

    var favoriteTracksLoading by mutableStateOf(false)
    var favoriteTracks: List<Track> by mutableStateOf(emptyList())

    var radiosLoading by mutableStateOf(false)
    var radios: List<Radio> by mutableStateOf(emptyList())
    var currentRadio by mutableStateOf(NULL_RADIO)
    var currentRadioLoading by mutableStateOf(false)
    var currentRadioTracks: List<Track> by mutableStateOf(emptyList())
    init {
        loadSearchHistoryFromLocalStorage()
        loadFavoriteTracksFromLocalStorage()
    }

    // track to be shown in the track options screen
    private var currentOptionsTrack by mutableStateOf(NULL_TRACK)
    var isTrackOptionsOpen by mutableStateOf(false)

    fun currentOptionsTrack() = currentOptionsTrack
    fun showTrackOptions(track: Track) {
        currentOptionsTrack = track
        isTrackOptionsOpen = true
    }
    fun closeTrackOptions() {
        isTrackOptionsOpen = false
    }

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
                favoriteTracks = trackIDs.map { id ->
                    deezerService.loadTrack(id)
                }
                favoriteTracksLoading = false
                // load album images
                favoriteTracks.forEach{ track ->
                    modelScope.launch { deezerService.lazyLoadImages(track.album) }
                }
            }
        }
    }
    fun toggleLike(track: Track) {
        if (isFavorite(track.id)) unlikeTrack(track)
        else likeTrack(track)
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

    private var nestedScreens: Map<MainMenu, List<Screen>> by mutableStateOf(emptyMap())
    // var currentNestedScreen: Map<MainMenu, Screen> by mutableStateOf(mapOf())
    private var currentMenu: MainMenu by mutableStateOf(MainMenu.SEARCH)

    fun currentMenu() = currentMenu
    fun setMenu(mainMenu: MainMenu) {
        // drop screen stack of current menu, when selecting current menu twice
        if (currentMenu == mainMenu) {
            nestedScreens = nestedScreens.minus(currentMenu)
        }
        currentMenu = mainMenu
    }
    fun getCurrentNestedScreen(defaultUI: @Composable () -> Unit): Screen {
        if (nestedScreens.getOrDefault(currentMenu, emptyList()).isEmpty()) return Screen(defaultUI, true, "")
        return nestedScreens[currentMenu]!!.last()
    }
    fun closeNestedScreen() {
        val currentScreenStack = nestedScreens.getOrDefault(currentMenu, emptyList())
        nestedScreens = nestedScreens.plus(Pair(currentMenu, currentScreenStack.dropLast(1)))
    }
    /**
     * Open a nested screen in the currentMenu
     * @param title screen title
     */
    fun openNestedScreen(title: String, ui: @Composable () -> Unit) {
        val currentScreenStack = nestedScreens.getOrDefault(currentMenu, emptyList())
        val screen = Screen(ui, true, title)
        nestedScreens = nestedScreens.plus(Pair(currentMenu, currentScreenStack.plus(screen)))
    }
    fun getPreviousScreenName(): String {
        val currentScreenStack = nestedScreens.getOrDefault(currentMenu, emptyList())
        return if (currentScreenStack.size < 2) currentMenu.title else currentScreenStack[currentScreenStack.lastIndex-1].screenName
    }

    var isSearchFocused by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var searchHistory: List<String> by mutableStateOf(emptyList())
    var searchTrackList: List<Track> by mutableStateOf(emptyList())
    var searchArtists: List<Artist> by mutableStateOf(emptyList())
    var searchAlbums: List<Album> by mutableStateOf(emptyList())

    fun searchText() = searchText
    fun searchText(value: String) {
        searchText = value
        if (searchText.isBlank()) clearSearchResult()
    }
    fun searchHistory() = searchHistory
    fun focusSearch() {
        isSearchFocused = true
    }
    fun clearSearchResult() {
        searchText = ""
        searchTrackList = emptyList()
        searchArtists = emptyList()
        searchAlbums = emptyList()
    }

    fun search() {
        if (searchText.length < 2) return
        isLoading = true
        // add term to search history
        addToSearchHistory(searchText)
        searchTrackList = emptyList()
        modelScope.launch {
            searchTrackList = deezerService.search(searchText)
            searchArtists = deezerService.uniqueArtists(searchTrackList).toList()
            searchAlbums = deezerService.uniqueAlbums(searchTrackList).toList()
            isLoading = false
            searchTrackList.forEach{
                deezerService.lazyLoadImages(it.album)
                deezerService.lazyLoadImages(it.artist)
            }
        }
    }
    fun deleteSearchHistory() {
        searchHistory = emptyList()
        persistSearchHistory()
    }
    fun deleteSearchTerm(term: String) {
        searchHistory = searchHistory.filter { it != term }
        persistSearchHistory()
    }

    private fun addToSearchHistory(term: String) {
        if (term.isNotBlank() && !searchHistory.contains(searchText)) {
            searchHistory = searchHistory.plus(searchText)
            persistSearchHistory()
        }
    }
    private fun persistSearchHistory() {
        modelScope.launch { storageService.writeSearchHistory(searchHistory) }
    }
    private fun loadSearchHistoryFromLocalStorage() {
        modelScope.launch {
            val searchTermsFlow = storageService.readSearchHistory()
            searchTermsFlow.collect { searchTerms ->
                waitUntilMutableStateReadable()
                searchHistory = searchTerms
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
data class Screen(val composeFunction: @Composable () -> Unit, val isOpeningTransition: Boolean, val screenName: String)
class ScreenTransition(val newScreen: () -> Unit, val oldScreen: (() -> Unit) ? = null, val forward: Boolean = true)