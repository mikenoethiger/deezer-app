package fhnw.emoba.freezerapp.ui

import android.util.Log
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
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.theme.PADDING_MEDIUM
import fhnw.emoba.freezerapp.ui.theme.PADDING_SMALL

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun AppUI(appModel: AppModel, playerModel: PlayerModel, artistModel: ArtistModel) {
    appModel.apply {
        MaterialTheme {
            var mainScreen: @Composable () -> Unit = { DeezerLogo() }
            when (currentMenu.ordinal) {
                MainMenu.FAVORITES.ordinal -> mainScreen = { FavoriteScreen(appModel, playerModel) }
                MainMenu.SEARCH.ordinal -> mainScreen = { SearchScreen(appModel, playerModel, artistModel = artistModel) }
                MainMenu.RADIO.ordinal -> mainScreen = { RadioScreen(appModel, playerModel) }
            }
            val currentScreen = getCurrentNestedScreen(defaultUI = mainScreen)
            // TODO add slide in / out animation between current and previous screen using the CrossfadeSlide function
            currentScreen.composeFunction()

            SlideUpVertically(visible = isPlayerOpen) {
                PlayerScreen(
                    appModel,
                    playerModel
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun FavoriteScreen(appModel: AppModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { DefaultTopBar(scaffoldState, "Favorite Songs") },
            drawerContent = { Drawer() },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { MessageBox(text = "Radio") },
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun RadioScreen(appModel: AppModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { DefaultTopBar(scaffoldState, "Radio Stations") },
            drawerContent = { Drawer() },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { MessageBox(text = "Radio") },
        )
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun SearchScreen(appModel: AppModel, playerModel: PlayerModel, artistModel: ArtistModel) {
    val scaffoldState = rememberScaffoldState()
    appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { SearchTextField(appModel) },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { SearchBody(appModel = appModel, playerModel = playerModel, artistModel = artistModel) },
            drawerContent = { Drawer() },
            floatingActionButtonPosition = FabPosition.End,
        )
    }
}

@Composable
private fun DefaultTopBar(scaffoldState: ScaffoldState, title: String) {
    TopAppBar(title = { Text(title) }, navigationIcon = { DrawerIcon(scaffoldState) })
}

@Composable
private fun MessageBox(text: String) {
    Box(alignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.h3
        )
    }
}


@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun SearchBody(appModel: AppModel, playerModel: PlayerModel, artistModel: ArtistModel) {
    Log.d("render", "SearchBody")
    appModel.apply {
        when {
            isLoading -> LoadingBox("Searching tracks...")
            searchTrackList.isEmpty() -> CenteredDeezerLogo()
            else -> {
                LazyTrackList(tracks = searchTrackList, trackListTitle = "Tracks", playerModel = playerModel, modifier = Modifier.padding(top = PADDING_MEDIUM)) {
                    ArtistListHorizontal(
                        appModel = appModel,
                        artists = searchArtists,
                        playerModel = playerModel,
                        artistModel = artistModel
                    )
                    DividerMedium()
                    AlbumListHorizontal(albums = searchAlbums)
                    DividerMedium()
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(appModel: AppModel) {
    appModel.apply {
        TextField(
            value = songsSearchText, onValueChange = { newValue -> songsSearchText = newValue }, placeholder = { Text("Search") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { IconButton(onClick = { songsSearchText = "" }) { Icon(Icons.Filled.Clear) } },
            imeAction = ImeAction.Search,
            onImeActionPerformed = { action, softwareController ->
                if (action == ImeAction.Search) {
                    softwareController?.hideSoftwareKeyboard()
                    searchSongs()
                }
            }
        )
    }

}