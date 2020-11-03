package fhnw.emoba.freezerapp.data

import org.json.JSONObject
import org.json.JSONArray

class Track(json: JSONObject) {

    val id             = json.getInt("id") // The track's Deezer id
    val readable       = json.getBoolean("readable") // true if the track is readable in the player for the current user
    val title          = json.getString("title") // The track's fulltitle
    val titleShort     = json.getString("title_short") // The track's short title
    val link           = json.getString("link") // The url of the track on Deezer
    val duration       = json.getInt("duration") // The track's duration in seconds
    val rank           = json.getInt("rank") // The track's Deezer rank
    val explicitLyrics = json.getBoolean("explicit_lyrics") // Whether the track contains explicit lyrics
    val preview        = json.getString("preview") // The url of track's preview file. This file contains the first 30 seconds of the track
    val artist         = Artist(json.getJSONObject("artist")) // artist object containing : id, name, link, picture, picture_small, picture_medium, picture_big, picture_xl
    val album          = if (json.has("album")) Album(json.getJSONObject("album")) else NULL_ALBUM
    val contributors   = if (json.has("contributors")) getContributors(json.getJSONArray("contributors")) else emptySet()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Track) return false
        return other.id == id
    }
    override fun hashCode(): Int  = id

    constructor(jsonString: String): this(JSONObject(jsonString))
}

fun getContributors(jsonArray: JSONArray): Set<Artist> = jsonArray.map { Artist(it) }.toSet()
fun getTracks(jsonArray: JSONArray): List<Track> = jsonArray.map { Track(it) }

// identified by id=-1
val NULL_TRACK = Track("{\"id\":-1,\"readable\":false,\"title\":\"\",\"title_short\":\"\",\"title_version\":\"\",\"link\":\"\",\"duration\":0,\"rank\":0,\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"preview\":\"\",\"md5_image\":\"\",\"artist\":{\"id\":-1,\"name\":\"\",\"link\":\"\",\"picture\":\"\",\"picture_small\":\"\",\"picture_medium\":\"\",\"picture_big\":\"\",\"picture_xl\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"album\":{\"id\":-1,\"title\":\"\",\"cover\":\"\",\"cover_small\":\"\",\"cover_medium\":\"\",\"cover_big\":\"\",\"cover_xl\":\"\",\"md5_image\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"type\":\"\"}")