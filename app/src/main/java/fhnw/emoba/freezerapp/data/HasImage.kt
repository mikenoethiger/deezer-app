package fhnw.emoba.freezerapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageAsset

abstract class HasImage {
    var imagesLoaded = false
    var imageX120: ImageAsset by mutableStateOf(ImageSize.x120.defaultImage)
    var imageX400: ImageAsset by mutableStateOf(ImageSize.x400.defaultImage)
    abstract fun getImageUrl(): String
    fun getImageUrl(imageSize: ImageSize): String = getImageUrl() + "?size=${imageSize.identifier}"
}