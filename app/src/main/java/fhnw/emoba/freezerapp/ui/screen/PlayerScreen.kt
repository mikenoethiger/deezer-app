package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.data.formatDuration
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.*
import fhnw.emoba.freezerapp.ui.theme.*

@Composable
fun PlayerScreen(model: ModelContainer) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(model) },
        bodyContent = { PlayerBody(model.playerModel) },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun PlayerBar(model: ModelContainer) {
    model.playerModel.apply {
        Row(
            modifier = Modifier.background(MaterialTheme.colors.primary)
                .clickable(onClick = { model.appModel.isPlayerOpen = true })
        ) {
            ImageFillHeight(asset = track().album.imageX120, height = IMAGE_SMALL)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(10.dp,0.dp,20.dp,0.dp).align(Alignment.CenterVertically)
            ) {
                val textColor = MaterialTheme.colors.onPrimary
                Column(Modifier.align(Alignment.CenterVertically)) {
                    SingleLineText(
                        track().title,
                        style = MaterialTheme.typography.subtitle1,
                        color = textColor
                    )
                    SingleLineText(
                        track().artist.name,
                        style = MaterialTheme.typography.subtitle2,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PlayPauseButton(
                        playerModel = model.playerModel,
                        circled = false,
                        color = textColor,
                    )
                    NextButton(
                        model = model.playerModel,
                        color = textColor
                    )
                }
            }
        }
    }
}


@Composable
private fun TopBar(model: ModelContainer) {
    model.playerModel.apply {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = { model.appModel.isPlayerOpen = false }),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleLineText(model.playerModel.trackListName())
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    model.appModel.isPlayerOpen = false
                }) { Icon(Icons.Filled.KeyboardArrowDown) }
            },
            actions = { LikeButton(model.appModel, model.playerModel.track()) })
    }
}

@Composable
private fun PlayerBody(model: PlayerModel) {
    model.apply {
        val track = track()
        Column(
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 30.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(asset = track.album.imageX400, modifier = Modifier.fillMaxWidth().fillMaxWidth().padding(bottom = 10.dp), contentScale = ContentScale.FillWidth)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                H5(text = track.title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                H6(text = "${track.artist.name} • ${track.album.title}", overflow = TextOverflow.Ellipsis, maxLines = 1)
                TimeSlider(model)
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    PlayerControls(model)
                }
            }
        }
    }
}

@Composable
private fun PlayerControls(model: PlayerModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(PADDING_LARGE), verticalAlignment = Alignment.CenterVertically) {
        PreviousButton(model, iconSize = ICON_LARGE-15.dp)
        PlayPauseButton(model, iconSize = ICON_LARGE)
        NextButton(model, iconSize = ICON_LARGE-15.dp)
    }
}

@Composable
fun PlayPauseButton(
    playerModel: PlayerModel,
    iconSize: Dp = ICON_SMALL,
    circled: Boolean = true,
    color: Color = MaterialTheme.colors.onBackground,
    modifier: Modifier = Modifier
) {
    playerModel.apply {
        val playIcon = if (circled) Icons.Filled.PlayCircleFilled else Icons.Filled.PlayArrow
        val pauseIcon = if (circled) Icons.Filled.PauseCircleFilled else Icons.Filled.Pause
        val icon = if (currentlyPlaying) pauseIcon else playIcon
        val enabled = isReady
        IconButton(
            onClick = { if (currentlyPlaying) pause() else play() },
            modifier = Modifier.size(iconSize).then(modifier),
            enabled = enabled
        ) {
            Image(
                icon.copy(defaultHeight = iconSize, defaultWidth = iconSize),
                colorFilter = ColorFilter.tint(if (enabled) color else color.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
private fun PreviousButton(model: PlayerModel, iconSize: Dp = ICON_SMALL, color: Color = MaterialTheme.colors.onBackground) {
    model.apply {
        IconButton(onClick = { previousTrack() }, modifier = Modifier.size(iconSize)) {
            Image(
                Icons.Filled.SkipPrevious.copy(defaultHeight = iconSize, defaultWidth = iconSize),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}

@Composable
private fun NextButton(model: PlayerModel, iconSize: Dp = ICON_SMALL, color: Color = MaterialTheme.colors.onBackground) {
    model.apply {
        IconButton(onClick = { nextTrack() }, modifier = Modifier.size(iconSize)) {
            Image(
                Icons.Filled.SkipNext.copy(defaultHeight = iconSize, defaultWidth = iconSize),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}

@Composable
private fun TimeSlider(model: PlayerModel) {
    model.apply {
        val track = track()
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