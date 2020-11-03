package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.IconImage
import fhnw.emoba.freezerapp.ui.theme.PADDING_SMALL

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
fun TrackOptions(model: ModelContainer) {
    Body(model = model)
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun Body(model: ModelContainer) {
    model.appModel.apply {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = { closeTrackOptions()})
        ) {
            Box{} // box is only to push down the second box, via SpaceBetween from parent
            OptionsBox(model)
        }
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun OptionsBox(model: ModelContainer) {
    model.appModel.apply {
        Box(modifier = Modifier.background(MaterialTheme.colors.background)){
            Column(modifier = Modifier.padding(20.dp)) {
                Header(model)
                Options(model)
            }
        }
    }
}

@Composable
private fun Header(model: ModelContainer, modifier: Modifier=Modifier) {
    model.appModel.apply {
        val track = currentOptionsTrack()
        ListItem(
            text = { Text(track.titleShort) },
            secondaryText = { Text(track.artist.name) },
            icon = { Image(asset = track.album.imageX120) },
            trailing = {
                IconButton(onClick = { closeTrackOptions() }) {
                    IconImage(icon = Icons.Filled.Close)
                }
            }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
private fun Options(model: ModelContainer) {
    model.appModel.apply {
        val cornedRounding = 20.dp
        val track = currentOptionsTrack()
        OptionsListItem("Show Album", Icons.Filled.Album, onClick = {
            model.albumModel.loadAlbum(track.album.id)
            closeTrackOptions()
            isPlayerOpen = false
            openNestedScreen(title = track.album.title) { AlbumScreen(model = model) }
        }, RoundedCornerShape(topLeft = cornedRounding, topRight = cornedRounding))
        Divider()
        OptionsListItem("Show Artist", Icons.Filled.Person, onClick = {
            model.artistModel.setArtist(track.artist)
            closeTrackOptions()
            isPlayerOpen = false
            openNestedScreen(title = track.artist.name) { ArtistScreen(model = model) }
        })
        Divider()
        val isFavorite = isFavorite(track.id)
        val favoriteIcon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
        val favoriteText = if (isFavorite) "Un-Favorite" else "Favorite"
        OptionsListItem(favoriteText, favoriteIcon, onClick = {
            toggleLike(track)
            closeTrackOptions()
        }, cornerShape = RoundedCornerShape(bottomLeft = cornedRounding, bottomRight = cornedRounding))
    }
}

@Composable
private fun OptionsListItem(text: String, icon: VectorAsset, onClick: () -> Unit, cornerShape: Shape = RectangleShape) {
    ListItem(
        text = { Text(text) },
        icon = { IconImage(icon = icon, size=28.dp, color=Color.Black.copy(alpha=.8f), modifier=Modifier.padding(end= PADDING_SMALL)) },
        modifier = Modifier.clickable(onClick = onClick).height(60.dp).background(Color.Black.copy(alpha=.05f), cornerShape)
    )
}