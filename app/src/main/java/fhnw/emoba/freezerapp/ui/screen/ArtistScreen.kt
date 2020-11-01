package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.Album
import fhnw.emoba.freezerapp.data.formatNumber
import fhnw.emoba.freezerapp.model.AppModel
import fhnw.emoba.freezerapp.model.ArtistModel
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.*

@Composable
@ExperimentalAnimationApi
fun ArtistScreen(appModel: AppModel, artistModel: ArtistModel, playerModel: PlayerModel) {
    appModel.apply {
        Scaffold(
            topBar = { BackBar(text = artistModel.getArtist().name, onBack = { closeNestedScreen() }) },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { ArtistBody(appModel = appModel, artistModel = artistModel, playerModel = playerModel) },
        )
    }
}

@ExperimentalAnimationApi
@Composable
private fun ArtistBody(appModel: AppModel, artistModel: ArtistModel, playerModel: PlayerModel) {
    artistModel.apply {
        val artist = getArtist()
        Box {
            Image(asset = artist.pictureX400, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), contentScale = ContentScale.FillWidth)
            ScrollableColumn{
                Column(modifier = Modifier.padding(top = 200.dp)){}
                Column(modifier = Modifier
                    .background(VerticalGradient(listOf(Color.Transparent, MaterialTheme.colors.background), 0f, 350f))
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column {
                        H4(text = artist.name, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.padding(start = 10.dp))
                        val fans = formatNumber(artist.nbFan)
                        Subtitle1(text = "$fans fans", modifier = Modifier.padding(start = 10.dp))
                    }
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                    H5(text = "Top Tracks", modifier = Modifier.padding(start = 10.dp))
                    Column {
                        TrackList(tracks = artistModel.top5Tracks, playerModel = playerModel, subtitle = { track ->
                            "Rank ${formatNumber(track.rank)}"
                        }, showIndex = true)
                    }
                    H5(text = "Albums", modifier = Modifier.padding(start = 10.dp))
                    AlbumListHorizontal(albums = albums)
                    H5(text = "Similar Artists", modifier = Modifier.padding(start = 10.dp))
                    ArtistListHorizontal(
                        appModel = appModel,
                        playerModel = playerModel,
                        artistModel = artistModel,
                        artists = contributors,
                    )
                    H5(text = "More Tracks", modifier = Modifier.padding(start = 10.dp))
                    Column {
                        TrackList(tracks = artistModel.moreTracks, playerModel = playerModel, subtitle = { track ->
                            "Rank ${formatNumber(track.rank)}"
                        })
                    }
                }
            }
        }
    }
}