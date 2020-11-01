package fhnw.emoba.freezerapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.Track
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.screen.ArtistScreen
import fhnw.emoba.freezerapp.ui.theme.IMAGE_MIDDLE

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

@ExperimentalAnimationApi
@Composable
fun SearchScreen(appModel: AppModel, playerModel: PlayerModel, artistModel: ArtistModel) {
    val scaffoldState = rememberScaffoldState()
    appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
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

@ExperimentalAnimationApi
@Composable
private fun SearchBody(appModel: AppModel, playerModel: PlayerModel, artistModel: ArtistModel) {
    appModel.apply {
        Column {
            SearchTextField(
                value = songsSearchText,
                onValueChange = { newValue -> songsSearchText = newValue },
                onSearch = { searchSongs() })
            when {
                isLoading -> Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingBox("Searching tracks...")
                }
                searchTrackList.isEmpty() -> Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(30.dp).fillMaxHeight()
                ) { DeezerLogo() }
                else -> {
                    val commonPadding = 10.dp
                    ScrollableColumn {
                        H5(text = "Artists", modifier = Modifier.padding(commonPadding))
                        ArtistListHorizontal(
                            artists = searchArtists,
                            appModel = appModel,
                            playerModel = playerModel,
                            artistModel = artistModel,
                            modifier = Modifier.padding(start = commonPadding)
                        )
                        Divider(modifier = Modifier.padding(vertical = commonPadding))
                        H5(text = "Albums", modifier = Modifier.padding(commonPadding))
                        AlbumListHorizontal(
                            albums = searchAlbums,
                            modifier = Modifier.padding(start = commonPadding)
                        )
                        Divider(modifier = Modifier.padding(vertical = commonPadding))
                        H5(text = "Tracks", modifier = Modifier.padding(commonPadding))
                        TrackList(tracks = searchTrackList, playerModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(value: String, onValueChange: (String) -> Unit, onSearch: () -> Unit) {
    TextField(
        value = value, onValueChange = onValueChange, placeholder = { Text("Search") },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = { IconButton(onClick = { onValueChange("") }) { Icon(Icons.Filled.Clear) } },
        imeAction = ImeAction.Search,
        onImeActionPerformed = { action, softwareController ->
            if (action == ImeAction.Search) {
                softwareController?.hideSoftwareKeyboard()
                onSearch()
            }
        }
    )
}

@Composable
private fun LoadingBox(message: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(message, style = MaterialTheme.typography.h5)
        CircularProgressIndicator(modifier = Modifier.padding(10.dp))
    }
}