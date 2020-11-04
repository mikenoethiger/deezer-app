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

    var isPlayerOpen by mutableStateOf(false)

    var isFavoriteTracksLoading by mutableStateOf(false)
    var favoriteTracks: List<Track> by mutableStateOf(emptyList())

    var isRadiosLoading by mutableStateOf(false)
    var radios: List<Radio> by mutableStateOf(emptyList())
    var currentRadio by mutableStateOf(NULL_RADIO)
    var isCurrentRadioLoading by mutableStateOf(false)
    var currentRadioTracks: List<Track> by mutableStateOf(emptyList())

    private var searchText by mutableStateOf("")
    private var searchHistory: List<String> by mutableStateOf(emptyList())
    var isSearchLoading by mutableStateOf(false)
    var isSearchFocused by mutableStateOf(false)
    var searchTrackList: List<Track> by mutableStateOf(emptyList())
    var searchArtists: List<Artist> by mutableStateOf(emptyList())
    var searchAlbums: List<Album> by mutableStateOf(emptyList())

    // track to be shown in the track options screen
    private var currentOptionsTrack by mutableStateOf(NULL_TRACK)
    var isTrackOptionsOpen by mutableStateOf(false)

    init {
        loadSearchHistoryFromLocalStorage()
        loadFavoriteTracksFromLocalStorage()
    }

    /* TRACK OPTIONS MANAGEMENT */

    fun currentOptionsTrack() = currentOptionsTrack

    fun showTrackOptions(track: Track) {
        currentOptionsTrack = track
        isTrackOptionsOpen = true
    }

    fun closeTrackOptions() {
        isTrackOptionsOpen = false
    }

    /* RADIOS MANAGEMENT */

    fun lazyLoadRadios() {
        if (radios.isNotEmpty()) return
        isRadiosLoading = true
        modelScope.launch {
            radios = deezerService.loadRadios()
            isRadiosLoading = false
        }
    }

    fun setCurrentRadioAndLoadTrackList(radio: Radio) {
        isCurrentRadioLoading = true
        currentRadio = radio
        currentRadioTracks = emptyList()
        modelScope.launch {
            currentRadioTracks = deezerService.loadTracks(currentRadio)
            isCurrentRadioLoading = false
        }
    }

    /* FAVORITE TRACKS MANAGEMENT */

    fun toggleFavorite(track: Track) {
        if (isFavorite(track.id)) unfavorTrack(track)
        else favorTrack(track)
    }
    fun favorTrack(track: Track) {
        favoriteTracks = favoriteTracks.plus(track)
        modelScope.launch {
            storageService.writeFavoriteTracks(favoriteTracks.map { it.id })
        }
    }

    fun unfavorTrack(track: Track) {
        favoriteTracks = favoriteTracks.minus(track)
        modelScope.launch {
            storageService.writeFavoriteTracks(favoriteTracks.map { it.id })
        }
    }

    fun isFavorite(trackID: Int): Boolean = favoriteTracks.map { it.id }.contains(trackID)

    private fun loadFavoriteTracksFromLocalStorage() {
        isFavoriteTracksLoading = true
        modelScope.launch {
            val trackIdFlow = storageService.readFavoriteTracks()
            trackIdFlow.collect { trackIDs ->
                waitUntilMutableStateReadable()
                favoriteTracks = trackIDs.map { id ->
                    deezerService.loadTrack(id)
                }
                isFavoriteTracksLoading = false
            }
        }
    }

    /* SEARCH MANAGEMENT */

    fun searchText() = searchText
    fun searchTextSet(value: String) {
        searchText = value
        if (searchText.isBlank()) clearSearch()
    }
    fun searchHistory() = searchHistory
    fun focusSearch() {
        isSearchFocused = true
    }
    fun clearSearch() {
        searchText = ""
        searchTrackList = emptyList()
        searchArtists = emptyList()
        searchAlbums = emptyList()
    }

    fun search() {
        if (searchText.length < 2) return
        isSearchLoading = true
        // add term to search history
        addToSearchHistory(searchText)
        searchTrackList = emptyList()
        modelScope.launch {
            searchTrackList = deezerService.searchTracks(searchText)
            searchArtists =  deezerService.searchArtists(searchText)
            // searchArtists = deezerService.uniqueArtists(searchTrackList).toList()
            searchAlbums = deezerService.uniqueAlbums(searchTrackList).toList()
            isSearchLoading = false
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

    /* NESTED SCREEN MANAGEMENT */

    private var currentMenu: MainMenu by mutableStateOf(MainMenu.SEARCH)
    private var nestedScreens: Map<MainMenu, List<Screen>> by mutableStateOf(emptyMap())

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

    /* OTHER FUNCTIONS */

    fun lazyLoadImages(obj: HasImage) = modelScope.launch { deezerService.lazyLoadImages(obj) }

    private suspend fun waitUntilMutableStateReadable() {
        // The following IllegalStateException is thrown if mutableStateOf variables are accessed
        // from a coroutine shortly after the object has been initialized (e.g. in the init block):
        // "Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied"
        // The proper solution for this problem could not be found yet.
        // In this function we just loop as long as the IllegalStateException is thrown.
        var readable = false
        while (!readable) {
            try {
                isFavoriteTracksLoading = isFavoriteTracksLoading
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