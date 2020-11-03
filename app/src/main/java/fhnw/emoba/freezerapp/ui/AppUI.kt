package fhnw.emoba.freezerapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.screen.FavoriteScreen
import fhnw.emoba.freezerapp.ui.screen.PlayerScreen
import fhnw.emoba.freezerapp.ui.screen.RadiosScreen
import fhnw.emoba.freezerapp.ui.screen.SearchScreen
import fhnw.emoba.freezerapp.ui.theme.PADDING_MEDIUM

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun AppUI(model: ModelContainer) {
    model.appModel.apply {
        MaterialTheme {
            var mainScreen: @Composable () -> Unit = { DeezerLogo() }
            when (currentMenu.ordinal) {
                MainMenu.FAVORITES.ordinal -> mainScreen = { FavoriteScreen(model) }
                MainMenu.SEARCH.ordinal -> mainScreen = { SearchScreen(model) }
                MainMenu.RADIO.ordinal -> mainScreen = { RadiosScreen(model) }
            }
            val currentScreen = getCurrentNestedScreen(defaultUI = mainScreen)
            // TODO add slide in / out animation between current and previous screen using the CrossfadeSlide function
            currentScreen.composeFunction()

            SlideUpVertically(visible = isPlayerOpen) {
                PlayerScreen(model)
            }
        }
    }
}