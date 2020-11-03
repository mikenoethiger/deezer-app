package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.PADDING_MEDIUM

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun SearchScreen(model: ModelContainer) {
    val scaffoldState = rememberScaffoldState()
    model.appModel.apply {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { SearchTextField(model) },
            bottomBar = { MenuWithPlayBar(model) },
            bodyContent = { SearchBody(model) },
            drawerContent = { Drawer() },
            floatingActionButtonPosition = FabPosition.End,
        )
    }
}


@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun SearchBody(model: ModelContainer) {
    model.appModel.apply {
        when {
            isLoading -> LoadingBox("Searching tracks...")
            searchTrackList.isEmpty() -> CenteredDeezerLogo(modifier = Modifier.padding(bottom = 80.dp))
            else -> {
                LazyTrackList(
                    tracks = searchTrackList,
                    trackListName = "Search",
                    title = "Tracks",
                    playerModel = model.playerModel,
                    modifier = Modifier.padding(top = PADDING_MEDIUM)) {
                    ArtistListHorizontal(model = model, artists = searchArtists, currentScreenName = MainMenu.SEARCH.title)
                    DividerMedium()
                    AlbumListHorizontal(model = model, albums = searchAlbums, currentScreenName = MainMenu.SEARCH.title)
                    DividerMedium()
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(model: ModelContainer) {
    model.appModel.apply {
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