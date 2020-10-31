package fhnw.emoba.freezerapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
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
import fhnw.emoba.freezerapp.ui.theme.IMAGE_MIDDLE

@ExperimentalAnimationApi
@Composable
fun AppUI(appModel: AppModel, playerModel: PlayerModel) {
    appModel.apply {
        MaterialTheme {
            var mainScreen: @Composable () -> Unit = {}
            when (selectedTab.ordinal) {
                MainMenu.FAVORITES.ordinal -> mainScreen = {FavoriteScreen(appModel, playerModel)}
                MainMenu.SEARCH.ordinal -> mainScreen = {SearchScreen(appModel, playerModel)}
                MainMenu.RADIO.ordinal -> mainScreen = {RadioScreen(appModel, playerModel)}
            }
            screenStack
            println("updating main screen")
            val openSubScreen = getOpenSubScreen(selectedTab)
            val closedSubScreen = getClosedSubScreen(selectedTab)
            if (openSubScreen == null && closedSubScreen == null) mainScreen()
            if (openSubScreen != null && closedSubScreen == null) {
                // transition from main to sub screen
                SlideOutLeft(mainScreen)
                SlideInRight(openSubScreen)
            }

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
@ExperimentalAnimationApi
fun InterpretScreen(artist: Track.Artist, appModel: AppModel, playerModel: PlayerModel) {
    appModel.apply {
        Scaffold(
            topBar = { TopAppBar(title= { Text(artist.name)}, navigationIcon = { Icon(Icons.Filled.ArrowBack) }) },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { Text("To be implemented") },
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
                isLoading -> Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
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
                        ArtistList(artists = searchArtists, appModel = appModel, playerModel = playerModel, modifier = Modifier.padding(start = commonPadding))
                        Divider(modifier = Modifier.padding(vertical = commonPadding))
                        H5(text = "Albums", modifier = Modifier.padding(commonPadding))
                        AlbumList(albums = searchAlbums, modifier = Modifier.padding(start = commonPadding))
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
private fun ArtistList(appModel: AppModel, playerModel: PlayerModel, artists: List<Track.Artist>, modifier: Modifier = Modifier) {
    appModel.apply {
        LazyRowFor(items = artists, modifier = modifier) { artist ->
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 10.dp).width(IMAGE_MIDDLE)
                    .clickable(onClick = { openScreen{ InterpretScreen(artist, appModel, playerModel) } }),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ImageFillWidth(asset = artist.pictureX400, width = IMAGE_MIDDLE)
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.subtitle1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AlbumList(albums: List<Track.Album>, modifier: Modifier = Modifier) {
    HorizontalItemList(albums, { it.title }, { it.coverX400 }, IMAGE_MIDDLE, modifier = modifier)
}

@Composable
private fun <T> HorizontalItemList(
    items: List<T>,
    getText: (T) -> String,
    getImage: (T) -> ImageAsset,
    imageSize: Dp = IMAGE_MIDDLE,
    modifier: Modifier = Modifier
) {
    LazyRowFor(items = items, modifier = modifier) { item ->
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp).width(imageSize),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImageFillWidth(asset = getImage(item), width = imageSize)
            Text(
                text = getText(item),
                style = MaterialTheme.typography.subtitle1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TrackList(tracks: List<Track>, playerModel: PlayerModel) {
    tracks.forEach { track ->
        track.album.coverX120
        TrackListItem(track = track, onTrackClick = {
            playerModel.trackList = tracks
            playerModel.loadTrack(track)
            playerModel.play()
        })
    }
}

@Composable
private fun TrackListItem(track: Track, onTrackClick: () -> Unit = {}) {
    ListItem(
        text = { Text(track.title) },
        secondaryText = { Text(track.artist.name) },
        icon = {
            Image(asset = track.album.coverX120)
        },
        trailing = { IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert) } },
        modifier = Modifier.clickable(onClick = onTrackClick)
    )
    Divider()
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