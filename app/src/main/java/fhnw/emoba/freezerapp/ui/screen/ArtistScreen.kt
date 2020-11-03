package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.asDisposableClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AnimationClockAmbient
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
    model.apply {
        // create a new LazyListState to reset scroll position for the LazyTrackList whenever the artist screen is recomposed
        // otherwise we stay at the same scroll position, when for example navigating from one artist to another
        val lazyListState = LazyListState(0, 0, defaultFlingConfig(), AnimationClockAmbient.current.asDisposableClock())
        Scaffold(
            topBar = { PreviousScreenBar(text = artistModel.getArtist().name,
                onBack = {
                    model.artistModel.setPreviousArtist()
                    appModel.closeNestedScreen()
                }, model=appModel) },
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
                title = null,
                playerModel = model.playerModel,
                lazyListState = lazyListState) {
                // make some space to show the background image when not scrolled down
                Box(modifier = Modifier.padding(top = 250.dp)){}
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
            .background(VerticalGradient(listOf(Color.Transparent, MaterialTheme.colors.background), 0f, 300f))
            .padding(top = PADDING_MEDIUM)
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM)
        ) {
            InterpretTitle(artist)
            TopTracks(model = model)
            AlbumListHorizontal(model=model, albums = albums, currentScreenName = artist.name)
            DividerThin()
            ArtistListHorizontal(
                model = model,
                artists = contributors,
                title="Similar Artists",
                currentScreenName = artist.name
            )
            DividerThin()
            H5("More Tracks", modifier = Modifier.padding(start= PADDING_SMALL, bottom = PADDING_SMALL))
        }
    }
}

@Composable
private fun InterpretTitle(artist: Artist) {
    Column {
        H4(text = artist.name, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.padding(start = PADDING_SMALL))
        val fans = formatNumber(artist.nbFan)
        Subtitle1(text = "$fans fans", modifier = Modifier.padding(start = 10.dp))
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
                    playerModel = model.playerModel,
                    subtitle = { t -> "Rank ${formatNumber(t.rank)}" },
                    showIndex = true)
            }
        }
    }
}