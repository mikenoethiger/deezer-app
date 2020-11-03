package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.PADDING_MEDIUM

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun FavoriteScreen(model: ModelContainer) {
    val scaffoldState = rememberScaffoldState()
    model.appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { DefaultTopBar(title = "Favorites", icon = MainMenu.FAVORITES.icon) },
            bottomBar = { MenuWithPlayBar(model) },
            bodyContent = { FavoriteBody(model = model) },
        )
    }
}

@ExperimentalLazyDsl
@Composable
private fun FavoriteBody(model: ModelContainer) {
    model.appModel.apply {
        if (favoriteTracks.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(bottom=70.dp)
            ) {
                H4(text = "❤️")
                H4(text = "No Favorites Yet")
            }
        } else {
            LazyTrackList(
                model = model,
                tracks = favoriteTracks,
                trackListName = "Favorites"
            ) {}
        }
    }
}