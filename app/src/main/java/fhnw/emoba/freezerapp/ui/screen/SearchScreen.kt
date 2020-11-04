package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
            bottomBar = { MenuWithPlayBar(model) },
            bodyContent = { Body(model) },
        )
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun Body(model: ModelContainer) {
    model.appModel.apply {
        Column {
            SlideInVerticallyFromTop(visible = !isSearchFocused) {
                DefaultTopBar(title = "Search", icon=null)
            }
            SearchTextField(model, modifier = Modifier.padding(10.dp))
            if (!isSearchFocused) CenteredDeezerLogo(modifier = Modifier.padding(bottom = 100.dp))
            else SearchResult(model)
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
private fun SearchResult(model: ModelContainer) {
    model.appModel.apply {
        when {
            isSearchLoading -> LoadingBox("Searching...")
            searchTrackList.isEmpty() -> {
                SearchHistory(model)
            }
            else -> {
                LazyTrackList(
                    tracks = searchTrackList,
                    trackListName = "Search",
                    model = model,
                    modifier = Modifier.padding(top = PADDING_MEDIUM)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        H5(text = "Artists", fontWeight = FontWeight.Bold)
                        ArtistListHorizontal(model = model, artists = searchArtists)
                        Divider()
                        H5(text = "Albums", fontWeight = FontWeight.Bold)
                        AlbumListHorizontal(model = model, albums = searchAlbums)
                        Divider()
                        H5(text = "Tracks", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHistory(model: ModelContainer) {
    model.appModel.apply {
        if (searchHistory().isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(bottom=90.dp)
            ) {
                // Eyes: 👀
                // Music Notes: 🎵
                H4(text = "\uD83C\uDFB5")
                H5(text = "Search History Empty", fontWeight = FontWeight.Bold)
            }
        } else {
            Column(modifier = Modifier.padding(10.dp, 20.dp, 10.dp, 10.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth()
                ) {
                    H5(text = "Search History", fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { deleteSearchHistory() }) {
                        Text("Delete")
                    }
                }
                Divider()
                LazyColumnFor(items=searchHistory()) { searchTerm ->
                    ListItem(
                        text = { H6(searchTerm) },
                        trailing = {
                            IconButton(onClick = { deleteSearchTerm(searchTerm) }) {
                                Icon(asset = Icons.Filled.Clear)
                            }
                        },
                        modifier = Modifier.clickable (onClick = {
                            focusSearch()
                            searchTextSet(searchTerm)
                            search()
                        })
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(model: ModelContainer, modifier: Modifier = Modifier) {
    model.appModel.apply {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha=.08f)
            val widthModifier = if (isSearchFocused) Modifier else Modifier.fillMaxWidth()
            val placeHolderText = if (isSearchFocused) "Search" else "Tracks, Artists, Albums"
            TextField(
                value = searchText(),
                onValueChange = { newValue -> searchTextSet(newValue) },
                placeholder = { Text(placeHolderText) },
                leadingIcon = { Icon(asset = Icons.Filled.Search) },
                trailingIcon = {
                    IconButton(onClick = {
                        searchTextSet("")
                    }) { Icon(Icons.Filled.Clear) }
                },
                imeAction = ImeAction.Search,
                onTextInputStarted = { focusSearch() },
                onImeActionPerformed = { action, softwareController ->
                    if (action == ImeAction.Search) {
                        softwareController?.hideSoftwareKeyboard()
                        search()
                    }
                },
                activeColor = colorOnBackground(),
                inactiveColor = colorOnBackground(),
                backgroundColor = backgroundColor,
                shape = RoundedCornerShape(5.dp),
                modifier =  widthModifier
            )
            if (isSearchFocused) {
                Text(text = "Cancel", modifier = Modifier.clickable(onClick = {
                    clearSearch()
                    isSearchFocused = false
                }))
            }
        }
    }

}