package fhnw.emoba.freezerapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.FailedResource
import androidx.compose.ui.res.LoadedResource
import androidx.compose.ui.res.PendingResource
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.model.FreezerModel

// Components

@Composable
fun BackBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack) } })
}

@Composable
fun DrawerIcon(scaffoldState: ScaffoldState) {
    IconButton(onClick = { scaffoldState.drawerState.open() }) {
        Icon(Icons.Filled.Menu)
    }
}

@Composable
fun Drawer(model: FreezerModel) {
    Column {
        Text("Empty Drawer")
    }
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
fun MaterialText(text: String, textStyle: TextStyle, modifier: Modifier = Modifier) = Text(text, style = textStyle, modifier = modifier)
@Composable
fun H1(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h1, modifier)
@Composable
fun H2(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h2, modifier)
@Composable
fun H3(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h3, modifier)
@Composable
fun H4(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h4, modifier)
@Composable
fun H5(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h5, modifier)
@Composable
fun H6(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.h6, modifier)
@Composable
fun Subtitle1(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.subtitle1, modifier)
@Composable
fun Subtitle2(text: String, modifier: Modifier = Modifier) = MaterialText(text, MaterialTheme.typography.subtitle2, modifier)
