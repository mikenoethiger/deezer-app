package fhnw.emoba.freezerapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.screen.*

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun AppUI(model: ModelContainer) {
    model.appModel.apply {
        MaterialTheme {
            var mainScreen: @Composable () -> Unit = { DeezerLogo() }
            when (currentMenu().ordinal) {
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

            SlideUpVertically(visible = isTrackOptionsOpen) {
                TrackOptions(model)
            }
        }
    }
}