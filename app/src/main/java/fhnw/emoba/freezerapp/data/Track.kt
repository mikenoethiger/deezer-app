package fhnw.emoba.freezerapp.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageAsset
import org.json.JSONObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.json.JSONArray

class Track(searchJSONObject: JSONObject) {

    // Structure described at https://developers.deezer.com/api/search
    class Album(albumObject: JSONObject) {
        val id = albumObject.getInt("id")
        val title = albumObject.getString("title")
        val coverLink = albumObject.getString("cover")
        val tracklist = albumObject.getString("tracklist")
        // view model will trigger cover image loading
        var coverX120: ImageAsset by mutableStateOf(ImageSize.x120.defaultImage)
        var coverX400: ImageAsset by mutableStateOf(ImageSize.x400.defaultImage)
        var coverX1000: ImageAsset by mutableStateOf(ImageSize.x1000.defaultImage)

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Album) return false
            return other.id == id
        }
        override fun hashCode(): Int  = id
    }
    class Artist(albumObject: JSONObject) {
        val id = albumObject.getInt("id")
        val name = albumObject.getString("name")
        val link = if (albumObject.has("link")) albumObject.getString("link") else ""
        val pictureLink = if (albumObject.has("picture")) albumObject.getString("picture") else ""
        // depending on the artist object, nbFan may be present or not. we make it a mutable state variable to give the service the chance to load nbFans at a later point
        var nbFan by mutableStateOf(if (albumObject.has("nb_fan")) albumObject.getInt("nb_fan") else 0)
        // view model will trigger the pricture loading
        var pictureX120: ImageAsset by mutableStateOf(ImageSize.x120.defaultImage)
        var pictureX400: ImageAsset by mutableStateOf(ImageSize.x400.defaultImage)
        val tracklist = albumObject.getString("tracklist")

        constructor(jsonString: String): this(JSONObject(jsonString))

        /**
         * Get trackList link for top `n` tracks
         * @param limit number of tracks to load
         * @param index offset, i.e. load `limit` tracks starting at `index`
         */
        fun getTrackListLink(limit: Int, index: Int): String {
            val baseUrl = tracklist.split("?")[0]
            return "$baseUrl?limit=$limit&index=$index"
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Artist) return false
            return other.id == id
        }
        override fun hashCode(): Int  = id
    }
    val id             = searchJSONObject.getInt("id") // The track's Deezer id
    val readable       = searchJSONObject.getBoolean("readable") // true if the track is readable in the player for the current user
    val title          = searchJSONObject.getString("title") // The track's fulltitle
    val titleShort     = searchJSONObject.getString("title_short") // The track's short title
    val link           = searchJSONObject.getString("link") // The url of the track on Deezer
    val duration       = searchJSONObject.getInt("duration") // The track's duration in seconds
    val rank           = searchJSONObject.getInt("rank") // The track's Deezer rank
    val explicitLyrics = searchJSONObject.getBoolean("explicit_lyrics") // Whether the track contains explicit lyrics
    val preview        = searchJSONObject.getString("preview") // The url of track's preview file. This file contains the first 30 seconds of the track
    val artist         = Artist(searchJSONObject.getJSONObject("artist")) // artist object containing : id, name, link, picture, picture_small, picture_medium, picture_big, picture_xl
    val album          = Album(searchJSONObject.getJSONObject("album")) // album object containing : id, title, cover, cover_small, cover_medium, cover_big, cover_xl
    val contributors   = if (searchJSONObject.has("contributors")) getContributors(searchJSONObject.getJSONArray("contributors")) else emptySet()

    private fun getContributors(jsonArray: JSONArray): Set<Artist> = jsonArray.map { Artist(it) }.toSet()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Track) return false
        return other.id == id
    }
    override fun hashCode(): Int  = id

    constructor(jsonString: String): this(JSONObject(jsonString))
}

// identified by id=-1
val NULL_TRACK = Track("{\"id\":-1,\"readable\":false,\"title\":\"\",\"title_short\":\"\",\"title_version\":\"\",\"link\":\"\",\"duration\":0,\"rank\":0,\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"preview\":\"\",\"md5_image\":\"\",\"artist\":{\"id\":-1,\"name\":\"\",\"link\":\"\",\"picture\":\"\",\"picture_small\":\"\",\"picture_medium\":\"\",\"picture_big\":\"\",\"picture_xl\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"album\":{\"id\":-1,\"title\":\"\",\"cover\":\"\",\"cover_small\":\"\",\"cover_medium\":\"\",\"cover_big\":\"\",\"cover_xl\":\"\",\"md5_image\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"type\":\"\"}")
val NULL_ARTIST = Track.Artist("{\"id\":-1,\"name\":\"\",\"link\":\"\",\"share\":\"\",\"picture\":\"\",\"nb_album\":0,\"nb_fan\":0,\"radio\":false,\"tracklist\":\"\",\"type\":\"\"}")