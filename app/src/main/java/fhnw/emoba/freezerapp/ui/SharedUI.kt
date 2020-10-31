package fhnw.emoba.freezerapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
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
import fhnw.emoba.freezerapp.model.AppModel
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.PlayerModel

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
            selectedTabIndex = selectedTab.ordinal
        ) {
            MainMenu.values().map { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon) }
                )
            }
        }
    }
}

// Pure components (i.e. model independent)

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
fun SlideInRight(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(initialOffsetX = {it}),
        exit = slideOutHorizontally(targetOffsetX = {0}),
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun SlideOutLeft(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(initialOffsetX = {0}),
        exit = slideOutHorizontally(targetOffsetX = {-it}),
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun SlideUpVertically(visible: Boolean, initialOffsetY: (Int) -> Int = {it}, targetOffsetY: (Int) -> Int = {it}, content: @Composable () -> Unit) {
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
fun MaterialText(text: String, textStyle: TextStyle, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = Text(text, style = textStyle, overflow = overflow, maxLines=maxLines, modifier = modifier)
@Composable
fun H1(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h1, overflow, maxLines, modifier)
@Composable
fun H2(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h2, overflow, maxLines, modifier)
@Composable
fun H3(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h3, overflow, maxLines, modifier)
@Composable
fun H4(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h4, overflow, maxLines, modifier)
@Composable
fun H5(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h5, overflow, maxLines, modifier)
@Composable
fun H6(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h6, overflow, maxLines, modifier)
@Composable
fun Subtitle1(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.subtitle1, overflow, maxLines, modifier)
@Composable
fun Subtitle2(text: String, overflow: TextOverflow = TextOverflow.Clip, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.subtitle2, overflow, maxLines, modifier)
