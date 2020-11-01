package fhnw.emoba.freezerapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.FailedResource
import androidx.compose.ui.res.LoadedResource
import androidx.compose.ui.res.PendingResource
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.R
import fhnw.emoba.freezerapp.data.NULL_TRACK
import fhnw.emoba.freezerapp.data.Track
import fhnw.emoba.freezerapp.model.AppModel
import fhnw.emoba.freezerapp.model.ArtistModel
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.PlayerModel
import fhnw.emoba.freezerapp.ui.screen.ArtistScreen
import fhnw.emoba.freezerapp.ui.theme.*
import org.w3c.dom.Text

// App components

@ExperimentalAnimationApi
@Composable
fun MenuWithPlayBar(appModel: AppModel, playerModel: PlayerModel) {
    Column {
        SlideUpVertically(visible = PlayerModel.currentTrack != NULL_TRACK) {
            PlayerBar(
                appModel,
                playerModel
            )
        }
        Divider(color = MaterialTheme.colors.primaryVariant, thickness = 2.dp)
        MenuBar(appModel)
    }
}

@Composable
fun MenuBar(model: AppModel) {
    model.apply {
        TabRow(
            selectedTabIndex = currentMenu.ordinal
        ) {
            MainMenu.values().map { tab ->
                Tab(
                    selected = currentMenu == tab,
                    onClick = { currentMenu = tab },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon) }
                )
            }
        }
    }
}

@Composable
fun BackBar(text: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text, overflow = TextOverflow.Ellipsis, maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.KeyboardArrowLeft)
            }
        })
}

/**
 * @param title to show above the horizontal list or null if no title to be displayed
 */
@Composable
fun AlbumListHorizontal(
    albums: List<Track.Album>,
    title: String? = "Albums",
    modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.padding(start = PADDING_SMALL).then(modifier),
        verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
    ) {
        if (title != null) H5(text = title)
        HorizontalItemList(
            albums,
            onClick = {},
            { it.title },
            { it.coverX400 },
            IMAGE_MIDDLE
        )
    }
}

/**
 * @param title to show above the horizontal list or null if no title to be displayed
 */
@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun ArtistListHorizontal(
    appModel: AppModel,
    playerModel: PlayerModel,
    artistModel: ArtistModel,
    artists: List<Track.Artist>,
    modifier: Modifier = Modifier,
    title: String? = "Artists"
) {
    appModel.apply {
        Column(
            modifier = Modifier.padding(start = PADDING_SMALL).then(modifier),
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
        ) {
            if (title != null) H5(text = title)
            HorizontalItemList(
                artists,
                onClick = { artist ->
                    artistModel.setArtist(artist)
                    openNestedScreen {
                        ArtistScreen(
                            appModel = appModel,
                            artistModel = artistModel,
                            playerModel = playerModel
                        )
                    }
                },
                { it.name },
                { it.pictureX400 },
                IMAGE_MIDDLE
            )
        }
    }
}

@Composable
private fun <T> HorizontalItemList(
    items: List<T>,
    onClick: (T) -> Unit,
    text: (T) -> String,
    image: (T) -> ImageAsset,
    imageSize: Dp = IMAGE_MIDDLE,
    modifier: Modifier = Modifier
) {
    LazyRowFor(items = items, modifier = modifier) { item ->
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp).width(imageSize)
                .clickable(onClick = { onClick(item) }),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImageFillWidth(asset = image(item), width = imageSize)
            Text(
                text = text(item),
                style = MaterialTheme.typography.subtitle1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

/**
 * @param tracksShowIndex whether the tracks in the list should be numbered
 * @param firstItem composable which will be rendered as first item in the scrollable list.
 *                  Addresses the case, when you want to scroll the whole page and not just
 *                  where the track list begins (which may be way down at the bottom of the screen)
 */
@ExperimentalLazyDsl
@Composable
fun LazyTrackList(
    playerModel: PlayerModel,
    tracks: List<Track>,
    trackListTitle: String? = null,
    tracksSubtitle: (Track) -> String = { it.artist.name },
    tracksShowIndex: Boolean = false,
    modifier: Modifier = Modifier,
    firstItem: @Composable (() -> Unit)? = null
) {
    LazyColumn(modifier=modifier) {
        if (firstItem != null) {
            item { firstItem() }
        }
        if (trackListTitle != null) item { H5(trackListTitle, modifier = Modifier.padding(start= PADDING_SMALL, bottom = PADDING_SMALL)) }
        itemsIndexed(tracks) { index, track ->
            TrackListItem(track = track, trackList=tracks, index=index, playerModel = playerModel)
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    trackList: List<Track>,
    index: Int,
    subtitle: (Track) -> String = { it.artist.name },
    showIndex: Boolean = false,
    playerModel: PlayerModel
) {
    ListItem(
        text = { SingleLineText(track.title) },
        secondaryText = { SingleLineText(subtitle(track)) },
        icon = {
            Row {
                if (showIndex) Text(
                    text = "${index + 1}",
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp)
                )
                Image(asset = track.album.coverX120)
            }
        },
        trailing = { IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert) } },
        modifier = Modifier.background(MaterialTheme.colors.background).clickable(onClick = {
            playerModel.trackList = trackList
            playerModel.loadTrack(track)
            playerModel.play()
        })
    )
    Divider(modifier = Modifier.background(MaterialTheme.colors.background))
}

// Pure components (i.e. model independent)

@Composable
fun DividerThin(modifier: Modifier = Modifier) = Divider(thickness = THICKNESS_THIN, modifier = Modifier.padding(vertical = PADDING_MEDIUM).then(modifier))
@Composable
fun DividerMedium(modifier: Modifier = Modifier) = Divider(thickness = THICKNESS_MEDIUM, modifier = Modifier.padding(vertical = PADDING_MEDIUM).then(modifier))
@Composable
fun DividerFat(modifier: Modifier = Modifier) = Divider(thickness = THICKNESS_FAT, modifier = Modifier.padding(vertical = PADDING_MEDIUM).then(modifier))

@Composable
fun LoadingBox(message: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(message, style = MaterialTheme.typography.h5)
        CircularProgressIndicator(modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun SingleLineText(
    title: String,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    style: TextStyle = AmbientTextStyle.current,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier) {
    Text(title, overflow = overflow, maxLines = 1, style=style, color=color, modifier = modifier)
}

@Composable
fun ImageFillHeight(asset: ImageAsset, height: Dp = 0.dp) {
    val modifier = if (height > 0.dp) Modifier.height(height) else Modifier
    return Image(asset = asset, contentScale = ContentScale.FillHeight, modifier = modifier)
}

@Composable
fun ImageFillWidth(asset: ImageAsset, width: Dp = 0.dp) {
    val modifier = if (width > 0.dp) Modifier.height(width) else Modifier
    return Image(asset = asset, contentScale = ContentScale.FillWidth, modifier = modifier)
}

@Composable
fun DrawerIcon(scaffoldState: ScaffoldState) {
    IconButton(onClick = { scaffoldState.drawerState.open() }) {
        Icon(Icons.Filled.Menu)
    }
}

@Composable
fun Drawer() {
    Column {
        Text("Empty Drawer")
    }
}

@Composable
fun CenteredDeezerLogo() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(30.dp).fillMaxHeight()
    ) { DeezerLogo() }

}

@Composable
fun DeezerLogo() {
    ImageLoadInBackground(resId = R.drawable.deezerlogo)
}

@Composable
private fun ImageLoadInBackground(@DrawableRes resId: Int) {
    val deferredResource = loadImageResource(id = resId)
    val resource = deferredResource.resource

    when (resource) {
        is LoadedResource -> {
            val imageAsset = resource.resource!!
            Image(
                asset = imageAsset,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.preferredHeight(200.dp),
                alignment = Alignment.TopCenter
            )
        }
        is FailedResource -> {
            Box(
                modifier = Modifier.fillMaxWidth().preferredHeight(200.dp),
                alignment = Alignment.Center
            ) {
                Text("failed", style = MaterialTheme.typography.h6, color = Color.Red)
            }
        }
        is PendingResource -> {
            Box(
                modifier = Modifier.fillMaxWidth().preferredHeight(200.dp),
                alignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// Animations

@ExperimentalAnimationApi
@Composable
fun SlideUpVertically(
    visible: Boolean,
    initialOffsetY: (Int) -> Int = { it },
    targetOffsetY: (Int) -> Int = { it },
    content: @Composable () -> Unit
) {
    // in order to slide from bottom to top, initial and target offset both have to be the content's height
    // specify animation duration by adding `animSpec = tween(durationMillis = 100)` to slideInVertically/slideOutVertically
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = initialOffsetY),
        exit = slideOutVertically(targetOffsetY = targetOffsetY),
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun FadeInOut(visible: Boolean, initialAlpha: Float = 0.4f, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(initialAlpha = initialAlpha),
        exit = fadeOut(targetAlpha = initialAlpha),
        content = content
    )
}

// Material shortcuts

@Composable
fun MaterialText(
    text: String,
    textStyle: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = Text(text, style = textStyle, overflow = overflow, maxLines = maxLines, modifier = modifier)

@Composable
fun H1(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h1, overflow, maxLines, modifier)

@Composable
fun H2(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h2, overflow, maxLines, modifier)

@Composable
fun H3(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h3, overflow, maxLines, modifier)

@Composable
fun H4(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h4, overflow, maxLines, modifier)

@Composable
fun H5(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h5, overflow, maxLines, modifier)

@Composable
fun H6(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h6, overflow, maxLines, modifier)

@Composable
fun Subtitle1(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.subtitle1, overflow, maxLines, modifier)

@Composable
fun Subtitle2(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.subtitle2, overflow, maxLines, modifier)
