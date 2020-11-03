package fhnw.emoba.freezerapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

class Artist(albumObject: JSONObject): HasImage(), HasTrackList {
    val id = albumObject.getInt("id")
    val name = albumObject.getString("name")
    val link = if (albumObject.has("link")) albumObject.getString("link") else ""
    val pictureLink = if (albumObject.has("picture")) albumObject.getString("picture") else ""
    // depending on the artist object, nbFan may be present or not. we make it a mutable state variable to give the service the chance to load nbFans at a later point
    val nbFan = if (albumObject.has("nb_fan")) albumObject.getInt("nb_fan") else 0
    private val trackListUrl = albumObject.getString("tracklist")

    override fun getTrackListUrl(): String = trackListUrl.split("?")[0]

    constructor(jsonString: String): this(JSONObject(jsonString))

    override fun getImageUrl(): String = pictureLink

    /**
     * Get trackList link for top `n` tracks
     * @param limit number of tracks to load
     * @param index offset, i.e. load `limit` tracks starting at `index`
     */
    fun getTrackListLink(limit: Int, index: Int): String {
        val baseUrl = trackListUrl.split("?")[0]
        return "$baseUrl?limit=$limit&index=$index"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Artist) return false
        return other.id == id
    }
    override fun hashCode(): Int  = id
}

val NULL_ARTIST = Artist("{\"id\":-1,\"name\":\"\",\"link\":\"\",\"share\":\"\",\"picture\":\"\",\"nb_album\":0,\"nb_fan\":0,\"radio\":false,\"tracklist\":\"\",\"type\":\"\"}")