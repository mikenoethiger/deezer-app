package fhnw.emoba.freezerapp.data

import org.json.JSONObject

// Structure described at https://developers.deezer.com/api/search
class Album(json: JSONObject) : HasImage(), HasTrackList {
    val id           = json.getInt("id")
    val title        = json.getString("title")
    val coverLink    = json.getString("cover")
    val tracklist    = json.getString("tracklist")
    val nbTracks     = if (json.has("nb_tracks")) json.getInt("nb_tracks") else 0
    val fans         = if (json.has("fans")) json.getInt("fans") else 0
    val duration     = if (json.has("duration")) json.getInt("duration") else 0
    val rating       = if (json.has("rating")) json.getInt("rating") else 0
    val releaseDate  = if (json.has("release_date")) json.getString("release_date") else ""
    val artist       = if (json.has("artist")) Artist(json.getJSONObject("artist")) else NULL_ARTIST
    val tracks       = if (json.has("tracks")) getTracks(json.getJSONObject("tracks").getJSONArray("data")) else emptyList()
    val contributors = if (json.has("contributors")) getContributors(json.getJSONArray("contributors")) else emptySet()

    constructor(jsonString: String): this(JSONObject(jsonString))

    fun releaseYear(): String = releaseDate.split("-")[0]

    override fun getImageUrl(): String = coverLink
    override fun getTrackListUrl(): String = tracklist

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Album) return false
        return other.id == id
    }
    override fun hashCode(): Int  = id
}

val NULL_ALBUM = Album("{\"id\":-1,\"title\":\"\",\"upc\":\"\",\"link\":\"\",\"share\":\"\",\"cover\":\"\",\"md5_image\":\"\",\"genre_id\":0,\"genres\":{\"data\":[]},\"label\":\"\",\"nb_tracks\":0,\"duration\":0,\"fans\":0,\"rating\":0,\"release_date\":\"1970-01-01\",\"record_type\":\"album\",\"available\":false,\"tracklist\":\"\",\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"contributors\":[],\"tracks\":{\"data\":[]}}")