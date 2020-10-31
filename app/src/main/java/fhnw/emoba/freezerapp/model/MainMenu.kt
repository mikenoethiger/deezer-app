package fhnw.emoba.freezerapp.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.VectorAsset

enum class MainMenu(val title: String, val icon: VectorAsset) {

    FAVORITES("Favorites", Icons.Filled.Favorite),
    SEARCH("Search", Icons.Filled.Search),
    RADIO("Radio", Icons.Filled.Radio)
}