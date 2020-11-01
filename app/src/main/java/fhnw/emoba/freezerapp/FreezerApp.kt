package fhnw.emoba.freezerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import fhnw.emoba.EmobaApp
import fhnw.emoba.freezerapp.data.impl.RemoteDeezerService
import fhnw.emoba.freezerapp.model.AppModel
import fhnw.emoba.freezerapp.model.ArtistModel
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.AppUI


object FreezerApp : EmobaApp {

    override fun initialize(activity: ComponentActivity, savedInstanceState: Bundle?) {
    }

    @ExperimentalAnimationApi
    @Composable
    override fun createAppUI() {
        val deezerService = RemoteDeezerService
        val playerModel = PlayerModel
        val appModel = AppModel(deezerService)
        val artistModel = ArtistModel(deezerService)
        AppUI(appModel, playerModel, artistModel)
    }

}

