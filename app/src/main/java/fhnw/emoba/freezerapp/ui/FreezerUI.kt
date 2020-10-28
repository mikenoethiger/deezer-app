package fhnw.emoba.freezerapp.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.SearchResult
import fhnw.emoba.freezerapp.model.FreezerModel

@Composable
fun AppUI(model: FreezerModel) {
    MaterialTheme {
        Scaffold(
            topBar = { Bar(model) },
            bodyContent = { Body(model) },
            floatingActionButtonPosition = FabPosition.End,
        )
    }
}

@Composable
private fun Bar(model: FreezerModel) {
    model.apply {
        TopAppBar(title = {
            Text(title)
        })
    }
}

@Composable
private fun TabBar(model: FreezerModel) {
    model.apply {
        TabRow(
            selectedTabIndex = selectedTab.ordinal
        ) {
            FreezerModel.Tab.values().map {
                Tab(
                    selected = selectedTab == it,
                    onClick = { selectedTab = it },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(text = it.text, style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}

@Composable
private fun Body(model: FreezerModel) {
    model.apply {
        Column {
            TabBar(model)
            when (selectedTab.ordinal) {
                FreezerModel.Tab.HITS.ordinal -> MessageBox(text = "Hits")
                FreezerModel.Tab.ALBUMS.ordinal -> MessageBox(text = "Albums")
                FreezerModel.Tab.RADIO.ordinal -> MessageBox(text = "Radio")
                FreezerModel.Tab.SONGS.ordinal -> SongsView(model)
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
private fun SongsView(model: FreezerModel) {
    model.apply {
        Column {
            SearchTextField(
                value = songsSearchText,
                onValueChange = { newValue -> songsSearchText = newValue },
                onSearch = { model.searchSongs() })
            if (isLoading) LoadingBox("Searching tracks...")
            else LazyColumnFor(items = songSearchResults) { SongListItem(model = it) }
        }
    }
}

@Composable
private fun SongListItem(model: SearchResult) {
    model.apply {
        ListItem(
            text = { Text(title) },
            secondaryText = { Text(artist.name) },
            icon = {
                Image(asset = album.cover)
            },
            trailing = { IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert) } },
            modifier = Modifier . clickable (onClick = { TODO("implement song click") })
        )
        Divider()
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