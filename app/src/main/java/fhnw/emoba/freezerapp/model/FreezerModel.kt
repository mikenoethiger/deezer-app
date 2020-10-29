package fhnw.emoba.freezerapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FreezerModel(val mainModel: MainModel) {

    val appTitle = "Freezer App"
    var currentScreen by mutableStateOf(Screen.HOME)

}