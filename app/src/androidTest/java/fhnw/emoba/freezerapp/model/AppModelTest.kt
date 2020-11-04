package fhnw.emoba.freezerapp.model

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.createDataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fhnw.emoba.freezerapp.data.NULL_TRACK
import fhnw.emoba.freezerapp.data.impl.LocalStorageService
import fhnw.emoba.freezerapp.data.impl.RemoteDeezerService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 *  View models often work asynchronous to perform a certain task, such as loading data.
 *  Testing asynchronous behaviour, requires to wait for some result at some point.
 *  Waiting inevitably leads to a join() on a thread/coroutine, which both seems to be undoable in JUnit 4.
 *  Doing a Thread.join() at the end of a test, results in a "Cancelled" test
 *  Doing a GlobalScope.launch{}.join in a test, results in a compile error which demands to add the "suspend" keyword to the function.
 *  Adding the suspend keyword to the function, leads to an InitializerError when trying to run the rest.
 *  Doing a GlobalScope.launch{} without a join, will successfully terminate the test before the coroutine has finished (potentially doing asserts which would fail)
 *
 *  As a consequence, I decided to neglect testing for all View Model functions which work asynchronously.
 */
@RunWith(AndroidJUnit4::class)
class AppModelTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("fhnw.freezerapp", appContext.packageName)
    }

    private fun createAppModel(): AppModel {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = appContext.createDataStore(name = "unittest_appmodel")
        return AppModel(RemoteDeezerService, LocalStorageService(dataStore))
    }
    @Test
    fun testTrackOptions() {
        // given
        val appModel = createAppModel()
        val track = NULL_TRACK
        // when
        appModel.showTrackOptions(track)
        // then
        assert(appModel.isTrackOptionsOpen)
        assert(track == appModel.currentOptionsTrack())
        // when
        appModel.closeTrackOptions()
        assert(!appModel.isTrackOptionsOpen)
    }

    @Test
    fun testFavorites() {
        // given
        val appModel = createAppModel()
        val track = NULL_TRACK
        // when
        appModel.favorTrack(track)
        // then
        assert(appModel.isFavorite(track.id))
        // when
        appModel.unfavorTrack(track)
        // then
        assert(!appModel.isFavorite(track.id))
        // when
        appModel.toggleFavorite(track)
        // then
        assert(appModel.isFavorite(track.id))
    }

    @Test
    fun search() {
        // given
        val appModel = createAppModel()
        val term     = "Eminem"
        // when
        appModel.searchTextSet(term)
        // then
        assert(appModel.searchText() == term)
        // when
        appModel.focusSearch()
        // then
        assert(appModel.isSearchFocused)
        // when
        appModel.clearSearch()
        assert(appModel.searchText().isBlank())
        assert(appModel.searchTrackList.isEmpty())
        assert(appModel.searchAlbums.isEmpty())
        assert(appModel.searchArtists.isEmpty())
    }

    @Test
    fun nestedScreensManagement() {
        // given
        val appModel = createAppModel()
        val name1 = "Screen1"
        val name2 = "Screen2"
        val screen1 = @Composable { Text(name1) }
        val screen2 = @Composable { Text(name2) }
        val identity = @Composable {}
        // when going to search menu
        appModel.setMenu(MainMenu.SEARCH)
        // then
        assert(appModel.currentMenu() == MainMenu.SEARCH)
        // when opening first screen
        appModel.openNestedScreen(name1, screen1)
        // then
        assert(appModel.getCurrentNestedScreen(identity).screenName == name1)
        assert(appModel.getPreviousScreenName() == MainMenu.SEARCH.title)
        // when opening second screen
        appModel.openNestedScreen(name2, screen2)
        // then
        assert(appModel.getCurrentNestedScreen(identity).screenName == name2)
        assert(appModel.getPreviousScreenName() == name1)
        // when going back to main menu
        appModel.setMenu(MainMenu.SEARCH)
        // then screen stack must be wiped
        assert(appModel.getCurrentNestedScreen(identity).screenName == "")
        // when opening first screen, change menu, open second screen
        appModel.openNestedScreen(name1, screen1)
        appModel.setMenu(MainMenu.RADIO)
        appModel.openNestedScreen(name2, screen2)
        // then
        assert(appModel.getCurrentNestedScreen(identity).screenName == name2)
        // when changing to previous menu
        appModel.setMenu(MainMenu.SEARCH)
        // then
        assert(appModel.getCurrentNestedScreen(identity).screenName == name1)
    }
}