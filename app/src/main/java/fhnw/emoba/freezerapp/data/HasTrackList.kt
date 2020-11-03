package fhnw.emoba.freezerapp.data

interface HasTrackList {
    fun getTrackListUrl(): String
    fun getTrackListUrl(limit: Int, index: Int = 0): String =  "${getTrackListUrl()}?limit=$limit&index=$index"
}