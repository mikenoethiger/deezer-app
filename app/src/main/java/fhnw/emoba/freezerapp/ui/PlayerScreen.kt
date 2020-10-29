package fhnw.emoba.freezerapp.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.formatDuration
import fhnw.emoba.freezerapp.model.FreezerModel
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.model.Screen

@Composable
fun PlayerScreen(freezerModel: FreezerModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { Bar(freezerModel, playerModel) },
        bodyContent = { Body(playerModel) },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun Bar(freezerModel: FreezerModel, playerModel: PlayerModel) {
    BackBar(
        title = playerModel.currentTrack.title,
        onBack = { freezerModel.currentScreen = Screen.HOME })
}

@Composable
private fun Body(model: PlayerModel) {
    model.apply {
        val track = currentTrack
        Column(
            modifier = Modifier.padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(asset = track.album.coverX1000, modifier = Modifier.fillMaxWidth())
            H5(text = track.title)
            H6(text = "${track.artist.name} • ${track.album.title}")
            TimeSlider(model)
            PlayerControls(model)
        }
    }
}

@Composable
private fun PlayerControls(model: PlayerModel) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        alignment = Alignment.Center
    ) {
        PreviousButton(model, Modifier.align(Alignment.CenterStart))
        PlayPauseButton(model, Modifier.align(Alignment.Center))
        NextButton(model, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun PlayPauseButton(model: PlayerModel, align: Modifier) {
    model.apply {
        val icon = if (currentlyPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
        val enabled = isReady

        IconButton(
            onClick = { if (currentlyPlaying) pause() else play() },
            modifier = Modifier.background(
                SolidColor(Color.LightGray),
                shape = CircleShape,
                alpha = if (enabled) 1.0f else 0.3f
            )
                .size(72.dp)
                .then(align),
            enabled = enabled
        ) {
            Icon(
                icon,
                tint = if (enabled) Color.Black else Color.LightGray,
                modifier = Modifier.size(48.dp).then(align)
            )
        }
    }
}

@Composable
private fun PreviousButton(model: PlayerModel, align: Modifier) {
    model.apply {
        IconButton(onClick = { previousTrack() }, modifier = align) {
            Image(Icons.Filled.SkipPrevious, modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun NextButton(model: PlayerModel, align: Modifier) {
    model.apply {
        IconButton(onClick = { nextTrack() }, modifier = align) {
            Image(Icons.Filled.SkipNext, modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun TimeSlider(model: PlayerModel) {
    model.apply {
        val track = currentTrack
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = formatDuration(currentMillis / 1000))
                Text(text = formatDuration(track.duration))
            }
            Slider(
                value = (currentMillis / 1000).toFloat(),
                valueRange = 0f..track.duration.toFloat(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}