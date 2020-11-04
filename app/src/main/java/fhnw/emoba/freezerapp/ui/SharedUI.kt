package fhnw.emoba.freezerapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.FailedResource
import androidx.compose.ui.res.LoadedResource
import androidx.compose.ui.res.PendingResource
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.R
import fhnw.emoba.freezerapp.data.*
import fhnw.emoba.freezerapp.model.*
import fhnw.emoba.freezerapp.ui.screen.AlbumScreen
import fhnw.emoba.freezerapp.ui.screen.ArtistScreen
import fhnw.emoba.freezerapp.ui.screen.PlayerBar
import fhnw.emoba.freezerapp.ui.theme.*

// App components

@Composable
fun PreviousScreenBar(model: AppModel, onBack: () -> Unit = {}) {
    val previousScreenName = model.getPreviousScreenName()
    val backAction = {
        onBack()
        model.closeNestedScreen()
    }
    TopAppBar(
        title = {
            SingleLineText(previousScreenName,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = backAction))
        },
        navigationIcon = {
            IconButton(onClick = backAction) { Icon(Icons.Filled.KeyboardArrowLeft) }
        })
}

@ExperimentalAnimationApi
@Composable
fun MenuWithPlayBar(model: ModelContainer) {
    model.playerModel.apply {
        Column {
            SlideInVerticallyFromBottom(visible = track() != NULL_TRACK) {
                PlayerBar(model)
            }
            Divider(color = MaterialTheme.colors.primaryVariant, thickness = 2.dp)
            MenuBar(model.appModel)
        }
    }
}

@Composable
fun MenuBar(model: AppModel) {
    model.apply {
        TabRow(
            selectedTabIndex = currentMenu().ordinal
        ) {
            MainMenu.values().map { tab ->
                Tab(
                    selected = currentMenu() == tab,
                    onClick = { setMenu(tab) },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon) }
                )
            }
        }
    }
}

@Composable
fun DefaultTopBar(title: String, icon: VectorAsset?, onIconClick: () -> Unit = {}) {
    TopAppBar(title = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) IconImage(icon = icon, color = MaterialTheme.colors.onPrimary)
            Text(title)
        }
    })
}

/**
 * @param title to show above the horizontal list or null if no title to be displayed
 */
@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
fun AlbumListHorizontal(
    model: ModelContainer,
    albums: List<Album>,
    modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
    ) {
        model.apply {
            HorizontalItemList(
                albums,
                onClick = { album ->
                    albumModel.loadAlbum(album.id)
                    appModel.openNestedScreen(album.title) {
                        AlbumScreen(model=model)
                    }
                },
                text = { it.title },
                imageSize = IMAGE_MEDIUM,
                model = model
            )
        }
    }
}

/**
 * @param title to show above the horizontal list or null if no title to be displayed
 */
@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun ArtistListHorizontal(
    model: ModelContainer,
    artists: List<Artist>,
    modifier: Modifier = Modifier,
) {
    model.apply {
        Column(
            modifier = Modifier.then(modifier),
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
        ) {
            HorizontalItemList(
                artists,
                onClick = { artist ->
                    artistModel.setArtist(artist)
                    appModel.openNestedScreen(title=artist.name) {
                        ArtistScreen(model = model)
                    }
                },
                text = { it.name },
                imageSize = IMAGE_MEDIUM,
                model = model
            )
        }
    }
}

@Composable
private fun <T : HasImage> HorizontalItemList(
    items: List<T>,
    onClick: (T) -> Unit,
    text: (T) -> String,
    imageSize: Dp = IMAGE_MEDIUM,
    model: ModelContainer,
    modifier: Modifier = Modifier
) {
    LazyRowFor(items = items, modifier = modifier) { item ->
        model.appModel.lazyLoadImages(item)
        val image = item.imageX400
        Column(
            modifier = Modifier.width(imageSize).padding(end=10.dp)
                .clickable(onClick = { onClick(item) }),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImageFillWidth(asset = image, width = imageSize, elevation = 10.dp)
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
 * @param showTrackIndex whether the tracks in the list should be numbered
 * @param firstItem composable which will be rendered as first item in the scrollable list.
 *                  Addresses the case, when you want to scroll the whole page and not just
 *                  where the track list begins (which may be way down at the bottom of the screen)
 */
@ExperimentalLazyDsl
@Composable
fun LazyTrackList(
    model: ModelContainer,
    tracks: List<Track>,
    trackListName: String,
    tracksSubtitle: (Track) -> String = { it.artist.name },
    showTrackIndex: Boolean = false,
    showImages: Boolean = true,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(), // provide your own list state if you want to control e.g. scroll position
    firstItem: @Composable (() -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier,
        state=lazyListState,
        contentPadding = PaddingValues(bottom=70.dp) // padding otherwise the list is hidden by the menu bar
    ) {
        if (firstItem != null) {
            item { firstItem() }
        }
        itemsIndexed(tracks) { index, track ->
            TrackListItem(model = model,
                track = track, trackList=tracks, trackListName = trackListName,
                index=index, subtitle = tracksSubtitle,
                showIndex = showTrackIndex,
                showImage = showImages
            )
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    trackList: List<Track>,
    trackListName: String,
    index: Int,
    subtitle: (Track) -> String = { it.artist.name },
    showIndex: Boolean = false,
    showImage: Boolean = true,
    model: ModelContainer
) {

    val icon: (@Composable () -> Unit)? = if (!showImage && !showIndex) null else {{
        TrackListItemIcon(track = track, index = index, showImage = showImage, showIndex = showIndex)
    }}
    model.appModel.lazyLoadImages(track.album)
    ListItem(
        text = { SingleLineText(track.title) },
        secondaryText = { SingleLineText(subtitle(track)) },
        icon = icon,
        trailing = { TrackOptionsButton(appModel = model.appModel, track = track, color = MaterialTheme.colors.onBackground) },
        modifier = Modifier.background(MaterialTheme.colors.background).clickable(onClick = {
            model.playerModel.setTrack(track, trackList, trackListName)
            model.playerModel.play()
        })
    )
    Divider(modifier = Modifier.background(MaterialTheme.colors.background))
}

@Composable
fun TrackListItemIcon(track: Track, index: Int, showIndex: Boolean = true, showImage: Boolean = true) {
    Row {
        if (showIndex) Text(
            text = "${index + 1}",
            modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp)
        )
        if (showImage) Image(asset = track.album.imageX120)
    }
}

@Composable
fun LikeButton(appModel: AppModel, track: Track, color: Color, modifier: Modifier = Modifier) {
    appModel.apply {
        val isLiked = isFavorite(track.id)
        IconButton(onClick = {
            toggleFavorite(track)
        }, modifier=modifier) {
            val icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
            Icon(icon, tint=color)
        }
    }
}

@Composable
fun TrackOptionsButton(appModel: AppModel, track: Track, color: Color, vertical: Boolean = true, modifier: Modifier = Modifier) {
    appModel.apply {
        IconButton(onClick = {
            showTrackOptions(track)
        }, modifier=modifier) {
            val icon = if (vertical) Icons.Filled.MoreVert else Icons.Filled.MoreHoriz
            Icon(icon, tint=color)
        }
    }
}

// Pure components (i.e. model independent)

@Composable
fun IconImage(
    icon: VectorAsset,
    color: Color = colorOnBackground(),
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Image(
        asset=icon.copy(defaultWidth = size, defaultHeight = size),
        colorFilter = ColorFilter.tint(color),
        modifier = modifier
    )
}

@Composable
fun LoadingBox(message: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().then(modifier)
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
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier) {
    Text(title, overflow = overflow, maxLines = 1, style=style, color=color, textAlign=textAlign, modifier = modifier)
}

@Composable
fun ImageFillHeight(asset: ImageAsset, height: Dp = 0.dp) {
    val modifier = if (height > 0.dp) Modifier.height(height) else Modifier
    return Image(asset = asset, contentScale = ContentScale.FillHeight, modifier = modifier)
}

@Composable
fun ImageFillWidth(asset: ImageAsset, width: Dp = 0.dp, elevation: Dp = 0.dp) {
    return Card(elevation = elevation) {
        Image(
            asset = asset,
            modifier = Modifier.width(width),
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Composable
fun Drawer() {
    Column {
        Text("Empty Drawer")
    }
}

@Composable
fun CenteredDeezerLogo(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(30.dp).fillMaxHeight().then(modifier)
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
            Image(asset = imageAsset)
        }
        is FailedResource -> {
            Box(alignment = Alignment.Center) {
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
fun SlideInVerticallyFromTop(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    SlideInVertically(
        visible = visible,
        initialOffsetY = { -it },
        targetOffsetY = { -it },
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun SlideInVerticallyFromBottom(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    SlideInVertically(
        visible = visible,
        initialOffsetY = { it },
        targetOffsetY = { it },
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun SlideInVertically(
    visible: Boolean,
    initialOffsetY: (Int) -> Int,
    targetOffsetY: (Int) -> Int,
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

// Colors

@Composable fun colorOnBackground() = MaterialTheme.colors.onBackground
@Composable fun colorBackground() = MaterialTheme.colors.background

@Composable
fun MaterialText(
    text: String,
    textStyle: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = Text(text, style = textStyle, overflow = overflow, maxLines = maxLines, modifier = modifier, fontWeight = fontWeight)

@Composable
fun H1(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h1, overflow, maxLines, fontWeight, modifier)

@Composable
fun H2(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h2, overflow, maxLines, fontWeight, modifier)

@Composable
fun H3(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h3, overflow, maxLines, fontWeight, modifier)

@Composable
fun H4(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h4, overflow, maxLines, fontWeight, modifier)

@Composable
fun H5(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h5, overflow, maxLines, fontWeight, modifier)

@Composable
fun H6(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.h6, overflow, maxLines, fontWeight, modifier)

@Composable
fun Subtitle1(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.subtitle1, overflow, maxLines, fontWeight, modifier)

@Composable
fun Subtitle2(
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) = MaterialText(text, MaterialTheme.typography.subtitle2, overflow, maxLines, fontWeight, modifier)
