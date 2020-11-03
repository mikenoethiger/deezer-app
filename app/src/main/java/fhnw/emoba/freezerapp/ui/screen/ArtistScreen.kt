package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.asDisposableClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.Artist
import fhnw.emoba.freezerapp.data.formatNumber
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.PADDING_MEDIUM
import fhnw.emoba.freezerapp.ui.theme.PADDING_SMALL

@ExperimentalLazyDsl
@Composable
@ExperimentalAnimationApi
fun ArtistScreen(model: ModelContainer) {
    model.artistModel.apply {
        // create a new LazyListState to reset scroll position for the LazyTrackList whenever the artist screen is recomposed
        // otherwise we stay at the same scroll position, when for example navigating from one artist to another
        val lazyListState = LazyListState(0, 0, defaultFlingConfig(), AnimationClockAmbient.current.asDisposableClock())
        Scaffold(
            topBar = { PreviousScreenBar(model=model.appModel, onBack = { setPreviousArtist() }) },
            bottomBar = { MenuWithPlayBar(model=model) },
            bodyContent = { ArtistBody(model=model, lazyListState = lazyListState) },
        )
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun ArtistBody(model: ModelContainer, lazyListState: LazyListState) {
    model.artistModel.apply {
        val artist = getArtist()
        Box {
            // background image (content will be above it)
            Image(asset = artist.imageX400, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), contentScale = ContentScale.FillWidth)
            LazyTrackList(
                tracks = getMoreTracks(),
                trackListName = artist.name,
                model = model,
                lazyListState = lazyListState) {
                // make some space to show the background image when not scrolled down
                Box(modifier = Modifier.padding(top = 300.dp)){}
                Header(model)
            }
        }
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun Header(model: ModelContainer) {
    model.artistModel.apply {
        val artist = getArtist()
        Column(modifier = Modifier
            .background(VerticalGradient(listOf(Color.Transparent, MaterialTheme.colors.background), 0f, 200f))
            .padding(start=10.dp, top=25.dp)
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM)
        ) {
            Title(artist)
            TopTracks(model = model)
            H5(text="Albums", fontWeight = FontWeight.Bold)
            AlbumListHorizontal(model=model, albums = albums)
            Divider()
            H5(text="Similar Artists", fontWeight = FontWeight.Bold)
            ArtistListHorizontal(model = model, artists = contributors)
            Divider()
            H5("More Tracks", modifier = Modifier.padding(start= PADDING_SMALL, bottom = PADDING_SMALL), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Title(artist: Artist) {
    Column {
        Text(artist.name, style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraBold), overflow = TextOverflow.Ellipsis, maxLines = 1)
        val fans = formatNumber(artist.nbFan)
        Subtitle1(text = "$fans fans")
    }
}

@Composable
private fun TopTracks(model: ModelContainer) {
    model.artistModel.apply {
        Column {
            val topTracks = getTopTracks()
            topTracks.forEachIndexed { index, track ->
                TrackListItem(
                    track = track,
                    trackList = trackList,
                    trackListName = getArtist().name,
                    index = index,
                    model = model,
                    subtitle = { t -> "Rank ${formatNumber(t.rank)}" },
                    showIndex = true)
            }
        }
    }
}