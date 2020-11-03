package fhnw.emoba.freezerapp.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fhnw.emoba.freezerapp.data.Radio
import fhnw.emoba.freezerapp.model.MainMenu
import fhnw.emoba.freezerapp.model.ModelContainer
import fhnw.emoba.freezerapp.ui.*

@ExperimentalLazyDsl
@ExperimentalAnimationApi
@Composable
fun RadiosScreen(model: ModelContainer) {
    val scaffoldState = rememberScaffoldState()
    model.appModel.apply {
        lazyLoadRadios()
        Scaffold(
            topBar = { DefaultTopBar(title="Radios", icon=MainMenu.RADIO.icon) },
            scaffoldState = scaffoldState,
            bottomBar = { MenuWithPlayBar(model) },
            bodyContent = { Body(model) },
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
private fun Body(model: ModelContainer) {
    model.appModel.apply {
        Column {
            if (radiosLoading) {
                LoadingBox(message = "Loading radios...")
            } else {
                LazyColumn {
                    items(items = radios) { radio ->
                        lazyLoadImages(radio)
                        RadioRow(model, radio)
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalLazyDsl
@Composable
private fun RadioRow(model: ModelContainer, radio: Radio) {
    model.appModel.apply {
        ListItem(
            text = { SingleLineText(radio.title) },
            icon = { Image(asset = radio.imageX120) },
            trailing = { IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert) } },
            modifier = Modifier.clickable(onClick = {
                setCurrentRadioAndLoadTrackList(radio)
                model.appModel.openNestedScreen(MainMenu.RADIO.title) { RadioScreen(model = model) }
            })
        )
        Divider(modifier = Modifier.background(MaterialTheme.colors.background))
    }
}