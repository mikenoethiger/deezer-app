package fhnw.emoba.freezerapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.formatDuration
import fhnw.emoba.freezerapp.model.AppModel
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.theme.IMAGE_SMALL

@Composable
fun PlayerScreen(appModel: AppModel, playerModel: PlayerModel) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { DefaultTopBar(appModel, playerModel) },
        bodyContent = { PlayerBody(playerModel) },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun PlayerBar(appModel: AppModel, playerModel: PlayerModel) {
    playerModel.apply {
        val textColor = MaterialTheme.colors.onPrimary
        Row(
            modifier = Modifier.background(MaterialTheme.colors.primary)
                .clickable(onClick = { appModel.isPlayerOpen = true })
        ) {
            ImageFillHeight(asset = currentTrack.album.coverX120, height = IMAGE_SMALL)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(10.dp,0.dp,20.dp,0.dp).align(Alignment.CenterVertically)
            ) {
                Column(Modifier.align(Alignment.CenterVertically)) {
                    SingleLineText(
                        currentTrack.title,
                        style = MaterialTheme.typography.subtitle1,
                        color = textColor
                    )
                    SingleLineText(
                        currentTrack.artist.name,
                        style = MaterialTheme.typography.subtitle2,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
                PlayPauseButton(
                    playerModel = playerModel,
                    circled = false,
                    color = textColor,
                    iconSize = 24.dp,
                    shapeSize = 24.dp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}


@Composable
private fun DefaultTopBar(appModel: AppModel, playerModel: PlayerModel) {
    TopAppBar(
        title = { SingleLineText(playerModel.currentTrack.title) },
        navigationIcon = {
            IconButton(onClick = {
                appModel.isPlayerOpen = false
            }) { Icon(Icons.Filled.ArrowDownward) }
        })
}

@Composable
private fun PlayerBody(model: PlayerModel) {
    model.apply {
        val track = currentTrack
        Column(
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 30.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(asset = track.album.coverX400, modifier = Modifier.fillMaxWidth().fillMaxWidth().padding(bottom = 10.dp), contentScale = ContentScale.FillWidth)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                H5(text = track.title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                H6(text = "${track.artist.name} • ${track.album.title}", overflow = TextOverflow.Ellipsis, maxLines = 1)
                TimeSlider(model)
                PlayerControls(model)
            }
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
        PlayPauseButton(model, modifier = Modifier.align(Alignment.Center))
        NextButton(model, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun PlayPauseButton(
    playerModel: PlayerModel,
    shapeSize: Dp = 72.dp,
    iconSize: Dp = 48.dp,
    circled: Boolean = true,
    color: Color = MaterialTheme.colors.onBackground,
    modifier: Modifier = Modifier
) {
    playerModel.apply {
        val icon = if (currentlyPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
        val enabled = isReady
        val background = if (circled) Modifier.background(
            SolidColor(Color.LightGray),
            shape = CircleShape,
            alpha = if (enabled) 1.0f else 0.3f
        ) else Modifier
        IconButton(
            onClick = { if (currentlyPlaying) pause() else play() },
            modifier = Modifier.size(shapeSize)
                .then(background)
                .then(modifier),
            enabled = enabled
        ) {
            Icon(
                icon,
                tint = if (enabled) color else color.copy(alpha = 0.6f),
                modifier = Modifier.size(iconSize).then(modifier),
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
            Slider(
                value = (currentMillis / 1000).toFloat(),
                valueRange = 0f..track.duration.toFloat(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = formatDuration(currentMillis / 1000))
                Text(text = formatDuration(track.duration))
            }
        }
    }
}