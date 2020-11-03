package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.*

@ExperimentalLazyDsl
@Composable
@ExperimentalAnimationApi
fun RadioScreen(model: ModelContainer) {
    model.appModel.apply {
        Scaffold(
            topBar = { PreviousScreenBar(model=model.appModel) },
            bottomBar = { MenuWithPlayBar(model = model) },
            bodyContent = { Body(model = model) },
        )
    }
}

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
private fun Body(model: ModelContainer) {
    model.appModel.apply {
        LazyTrackList(
            model = model,
            tracks = currentRadioTracks,
            trackListName = currentRadio.title
        ) {
            Column(
                modifier = Modifier.padding(start = PADDING_SMALL, top = PADDING_LARGE),
                verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM)
            ) {
                CoverImage(currentRadio.imageX400)
                H4(text = currentRadio.title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Subtitle1(text = "Radio • ${currentRadioTracks.size} Tracks")
                Divider()
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