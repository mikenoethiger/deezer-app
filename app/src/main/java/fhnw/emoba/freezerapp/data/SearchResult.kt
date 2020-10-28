package fhnw.emoba.freezerapp.data

import org.json.JSONObject

class SearchResult(searchJSONObject: JSONObject) {
    // Structure described at https://developers.deezer.com/api/search
    class Album(albumObject: JSONObject) {
        val id = albumObject.getInt("id")
        val title = albumObject.getString("title")
        val cover = albumObject.getString("cover")
        val tracklist = albumObject.getString("tracklist")
    }
    class Artist(albumObject: JSONObject) {
        val id = albumObject.getInt("id")
        val name = albumObject.getString("name")
        val link = albumObject.getString("link")
        val picture = albumObject.getString("picture")
        val tracklist = albumObject.getString("tracklist")
    }
    val id             = searchJSONObject.getInt("id") // The track's Deezer id
    val readable       = searchJSONObject.getBoolean("readable") // true if the track is readable in the player for the current user
    val title          = searchJSONObject.getString("title") // The track's fulltitle
    val titleShort     = searchJSONObject.getString("title_short") // The track's short title
    val titleVersion   = searchJSONObject.getString("title_version") // The track version
    val link           = searchJSONObject.getString("link") // The url of the track on Deezer
    val duration       = searchJSONObject.getInt("duration") // The track's duration in seconds
    val rank           = searchJSONObject.getLong("rank") // The track's Deezer rank
    val explicitLyrics = searchJSONObject.getBoolean("explicit_lyrics") // Whether the track contains explicit lyrics
    val preview        = searchJSONObject.getString("preview") // The url of track's preview file. This file contains the first 30 seconds of the track
    val artist         = Artist(searchJSONObject.getJSONObject("artist")) // artist object containing : id, name, link, picture, picture_small, picture_medium, picture_big, picture_xl
    val album          = Album(searchJSONObject.getJSONObject("album")) // album object containing : id, title, cover, cover_small, cover_medium, cover_big, cover_xl

}