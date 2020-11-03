package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.*
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.*

@ExperimentalLazyDsl
@Composable
@ExperimentalAnimationApi
fun AlbumScreen(model: ModelContainer) {
    model.appModel.apply {
        Scaffold(
            topBar = {
                PreviousScreenBar(text = model.artistModel.getArtist().name, onBack = {
                    closeNestedScreen()
                }, model=model.appModel)
            },
            bottomBar = { MenuWithPlayBar(model = model) },
            bodyContent = { Body(model = model) },
        )
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun Body(model: ModelContainer) {
    model.albumModel.apply {
        LazyTrackList(
            playerModel = model.playerModel,
            tracks = album.tracks,
            trackListName = album.title,
            title = null,
            showImages = false
        ) {
            Column(
                modifier = Modifier.padding(start = PADDING_SMALL, top = PADDING_LARGE),
                verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM)
            ) {
                CoverImage(album.imageX400)
                H4(text = album.title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Divider()
                val fans = formatNumber(album.fans)
                Subtitle1(text = "Album • ${album.releaseYear()} • $fans fans")
                val duration = formatDuration(album.duration, DurationFormat.READABLE)
                Subtitle2(text = "${album.nbTracks} Tracks • $duration")
                ArtistRow(model = model, album)
            }
        }
    }
}

@Composable
private fun CoverImage(image: ImageAsset) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Card(elevation = 20.dp ) {
            Image(
                asset = image,
                modifier = Modifier.width(IMAGE_LARGE),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
private fun ArtistRow(model: ModelContainer, album: Album) {
    model.appModel.apply {
        val artist = album.artist
        Column(modifier = Modifier.fillMaxWidth().clickable(onClick = {
            model.artistModel.setArtist(artist)
            openNestedScreen(album.title) { ArtistScreen(model = model) }
        }), verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)) {
            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(PADDING_MEDIUM)) {
                Card(shape = CircleShape) { Image(asset = artist.imageX120) }
                H6(text = artist.name, modifier = Modifier.align(Alignment.CenterVertically))
            }
            Divider()
        }
    }
}