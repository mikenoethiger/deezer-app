package fhnw.emoba.freezerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import fhnw.emoba.EmobaApp
import fhnw.emoba.freezerapp.data.impl.RemoteDeezerService
import fhnw.emoba.freezerapp.model.FreezerModel
import fhnw.emoba.freezerapp.model.MainModel
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.FreezerUI


object FreezerApp : EmobaApp {

    override fun initialize(activity: ComponentActivity, savedInstanceState: Bundle?) {
    }

    @ExperimentalAnimationApi
    @Composable
    override fun createAppUI() {
        val deezerService = RemoteDeezerService
        val playerModel = PlayerModel
        val homeModel = MainModel(deezerService, playerModel)
        FreezerUI(FreezerModel(homeModel), playerModel)
    }

}

