package fhnw.emoba.freezerapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.Track
import fhnw.emoba.freezerapp.model.FreezerModel
import fhnw.emoba.freezerapp.model.HomeTab
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.model.Screen

@ExperimentalAnimationApi
@Composable
fun FreezerUI(freezerModel: FreezerModel, playerModel: PlayerModel) {
    freezerModel.apply {
        MaterialTheme {
            SlideUpVertically(visible = currentScreen == Screen.PLAYER){ PlayerScreen(freezerModel, playerModel) }
            FadeInOut(visible = currentScreen == Screen.HOME) { HomeScreen(freezerModel, playerModel) }
        }
    }
}

@Composable
fun HomeScreen(model: FreezerModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    model.mainModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { Bar(scaffoldState, title = model.appTitle) },
            bodyContent = { Body(model, playerModel) },
            drawerContent = { Drawer(model) },
            floatingActionButtonPosition = FabPosition.End,
        )
    }
}

@Composable
private fun Bar(scaffoldState: ScaffoldState, title: String) {
    TopAppBar(title = { Text(title) }, navigationIcon = { DrawerIcon(scaffoldState) })
}

@Composable
private fun Body(model: FreezerModel, playerModel: PlayerModel) {
    model.mainModel.apply {
        Column {
            TabBar(model)
            when (selectedTab.ordinal) {
                HomeTab.HITS.ordinal -> MessageBox(text = "Hits")
                HomeTab.ALBUMS.ordinal -> MessageBox(text = "Albums")
                HomeTab.RADIO.ordinal -> MessageBox(text = "Radio")
                HomeTab.SONGS.ordinal -> SearchView(model, playerModel)
            }
        }
    }
}

@Composable
private fun TabBar(model: FreezerModel) {
    model.mainModel.apply {
        TabRow(
            selectedTabIndex = selectedTab.ordinal
        ) {
            HomeTab.values().map {
                Tab(
                    selected = selectedTab == it,
                    onClick = { selectedTab = it },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(text = it.title, style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}

@Composable
private fun MessageBox(text: String) {
    Box(modifier = Modifier.fillMaxSize(), alignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
private fun SearchView(freezerModel: FreezerModel, playerModel: PlayerModel) {
//    var visible by remember { mutableStateOf(true) }
    freezerModel.mainModel.apply {
        Column {
            SearchTextField(
                value = songsSearchText,
                onValueChange = { newValue -> songsSearchText = newValue },
                onSearch = { searchSongs() })
            if (isLoading) LoadingBox("Searching tracks...")
            else LazyColumnFor(items = searchTrackList) { track ->
                SongListItem(track = track, onTrackClick = {
                    playerModel.trackList = searchTrackList
                    playerModel.loadTrack(track)
                    playerModel.play()
                    freezerModel.currentScreen = Screen.PLAYER
                })
            }
        }
    }
}


@Composable
private fun SongListItem(track: Track, onTrackClick: () -> Unit = {}) {
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