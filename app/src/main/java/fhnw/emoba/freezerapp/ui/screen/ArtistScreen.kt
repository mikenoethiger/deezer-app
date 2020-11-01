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
            topBar = { BackBar(text = artistModel.artist.name, onBack = { closeNestedScreen() }) },
            bottomBar = { MenuWithPlayBar(appModel, playerModel) },
            bodyContent = { ArtistBody(artistModel = artistModel, playerModel = playerModel) },
        )
    }
}

@Composable
private fun ArtistBody(artistModel: ArtistModel, playerModel: PlayerModel) {
    val artist = artistModel.artist
    Box {
        Image(asset = artist.pictureX400, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), contentScale = ContentScale.FillWidth)
        ScrollableColumn{
            Column(modifier = Modifier.padding(top = 250.dp)){}
            Column(modifier = Modifier
                .background(VerticalGradient(listOf(Color.Transparent, MaterialTheme.colors.background), 0f, 10f))
                .fillMaxWidth()
            ) {
                H4(text = artist.name, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.padding(start = 10.dp))
                val fans = formatNumber(artist.nbFan)
                Subtitle1(text = "$fans fans", modifier = Modifier.padding(start = 10.dp))
                Divider()
                H6(text = "Top 5", modifier = Modifier.padding(start = 10.dp))
                TrackList(tracks = artistModel.top5Tracks, playerModel = playerModel, subtitle = { track ->
                    "Rank ${formatNumber(track.rank)}"
                }, showIndex = true)
            }
        }
    }
}


//Column(modifier = Modifier.padding(top = 250.dp)
//.background(VerticalGradient(listOf(Color.Transparent, MaterialTheme.colors.background), 0f, 350f))
//.padding(vertical = 20.dp)
//.fillMaxWidth()) {
//    H4(text = artist.name, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.padding(start = 10.dp))
//    val fans = formatNumber(artist.nbFan)
//    Subtitle1(text = "$fans fans", modifier = Modifier.padding(start = 10.dp))
//}
//Column(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxHeight()) {
//    Divider()
//    H6(text = "Top 5", modifier = Modifier.padding(start = 10.dp))
//    TrackList(tracks = artistModel.top5Tracks, playerModel = playerModel, subtitle = { track ->
//        "Rank ${formatNumber(track.rank)}"
//    }, showIndex = true)
//}