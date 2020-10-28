package fhnw.emoba.freezerapp.data

import org.json.JSONObject

class AlbumTrack(trackObject: JSONObject) {
    // Structure described at: https://developers.deezer.com/api/album
    val id             = trackObject.getInt("id") // The track's Deezer id
    val readable       = trackObject.getBoolean("readable") // true if the track is readable in the player for the current user
    val title          = trackObject.getString("title") // The track's fulltitle
    val titleShort     = trackObject.getString("title_short") // The track's short title
    val titleVersion   = trackObject.getString("title_version") // The track version
    val link           = trackObject.getString("link") // The url of the track on Deezer
    val duration       = trackObject.getInt("duration") // The track's duration in seconds
    val rank           = trackObject.getLong("rank") // The track's Deezer rank
    val explicitLyrics = trackObject.getBoolean("explicit_lyrics") // Whether the track contains explicit lyrics
    val preview        = trackObject.getString("preview") // The url of track's preview file. This file contains the first 30 seconds of the track
    val artist         = Artist(trackObject.getJSONObject("artist")) // artist object containing : id, name, link, picture, picture_small, picture_medium, picture_big, picture_xl
}