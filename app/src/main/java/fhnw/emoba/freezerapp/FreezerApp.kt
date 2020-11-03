package fhnw.emoba.freezerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.runtime.Composable
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import fhnw.emoba.EmobaApp
import fhnw.emoba.freezerapp.data.impl.LocalStorageService
import fhnw.emoba.freezerapp.data.impl.RemoteDeezerService
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.AppUI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object FreezerApp : EmobaApp {

    lateinit var dataStore: DataStore<Preferences>

    override fun initialize(activity: ComponentActivity, savedInstanceState: Bundle?) {
        dataStore = activity.createDataStore(name = "settings")
    }

    @ExperimentalLazyDsl
    @ExperimentalAnimationApi
    @Composable
    override fun createAppUI() {
        val storageService = LocalStorageService(dataStore)
        // uncomment to reset favorite tracks
        // GlobalScope.launch { storageService.writeFavoriteTracks(emptyList()) }
        val deezerService = RemoteDeezerService
        val playerModel = PlayerModel
        val appModel = AppModel(deezerService, storageService)
        val artistModel = ArtistModel(deezerService)
        val albumModel = AlbumModel(deezerService)
        val model = ModelContainer(appModel = appModel, artistModel = artistModel, albumModel = albumModel, playerModel = playerModel)
        AppUI(model)
    }
}

