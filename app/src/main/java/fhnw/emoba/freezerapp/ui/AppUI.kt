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
fun AppUI(appModel: AppModel, playerModel: PlayerModel) {
    appModel.apply {
        MaterialTheme {
            var mainScreen: @Composable () -> Unit = { DeezerLogo() }
            when (currentMenu.ordinal) {
                MainMenu.FAVORITES.ordinal -> mainScreen = { FavoriteScreen(appModel, playerModel) }
                MainMenu.SEARCH.ordinal -> mainScreen = { SearchScreen(appModel, playerModel) }
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
fun SearchScreen(appModel: AppModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { SearchBody(appModel, playerModel) },
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
private fun SearchBody(appModel: AppModel, playerModel: PlayerModel) {
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

@ExperimentalAnimationApi
@Composable
private fun ArtistListHorizontal(
    appModel: AppModel,
    playerModel: PlayerModel,
    artists: List<Track.Artist>,
    modifier: Modifier = Modifier
) {
    appModel.apply {
        HorizontalItemList(
            items = artists,
            onClick = {
                val artistModel = createArtistModel(it)
                openNestedScreen {
                    ArtistScreen(
                        appModel = appModel,
                        artistModel = artistModel,
                        playerModel = playerModel
                    )
                }
            },
            text = { it.name },
            image = { it.pictureX400 },
            imageSize = IMAGE_MIDDLE,
            modifier = modifier
        )
    }
//    appModel.apply {
//        LazyRowFor(items = artists, modifier = modifier) { artist ->
//            Column(
//                modifier = Modifier.padding(0.dp, 0.dp, 10.dp).width(IMAGE_MIDDLE)
//                    .clickable(onClick = { openNestedScreen{ ArtistScreen(artist, appModel, playerModel) } }),
//                verticalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                ImageFillWidth(asset = artist.pictureX400, width = IMAGE_MIDDLE)
//                Text(
//                    text = artist.name,
//                    style = MaterialTheme.typography.subtitle1,
//                    overflow = TextOverflow.Ellipsis,
//                    maxLines = 1
//                )
//            }
//        }
//    }
}

@Composable
private fun AlbumListHorizontal(albums: List<Track.Album>, modifier: Modifier = Modifier) {
    HorizontalItemList(albums, onClick={}, { it.title }, { it.coverX400 }, IMAGE_MIDDLE, modifier = modifier)
}

@Composable
private fun <T> HorizontalItemList(
    items: List<T>,
    onClick: (T) -> Unit,
    text: (T) -> String,
    image: (T) -> ImageAsset,
    imageSize: Dp = IMAGE_MIDDLE,
    modifier: Modifier = Modifier
) {
    LazyRowFor(items = items, modifier = modifier) { item ->
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp).width(imageSize).clickable(onClick = {onClick(item)}),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImageFillWidth(asset = image(item), width = imageSize)
            Text(
                text = text(item),
                style = MaterialTheme.typography.subtitle1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
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