package fhnw.emoba.freezerapp.data.impl

import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import fhnw.emoba.freezerapp.data.*
import org.json.JSONObject
import java.lang.StringBuilder

object RemoteDeezerService : DeezerService {
    private const val baseURL = "https://api.deezer.com"
    private const val baseURLAlbum = "$baseURL/album"
    private const val baseURLArtist = "$baseURL/artist"

    override fun search(query: String, fuzzyMode: Boolean): List<Track> {
        // early return
        if (query.isBlank()) return emptyList()
        val q = URLEncode("\"$query\"")
        return apiSearch(q, fuzzyMode)
    }

    override fun extendedSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean): List<Track> {
        // early return
        if (queryParameters.isEmpty()) return emptyList()
        // validation
        val validParams = arrayOf("artist", "album", "track", "label", "dur_min", "dur_max", "bpm_min", "bpm_max")
        queryParameters.keys.forEach { if (it !in validParams) error("Invalid parameter '$it'. Use any of: $validParams") }
        // build url
        val q = StringBuilder()
        queryParameters.forEach{ q.append("${it.key}:\"${it.value}\" ")}
        return apiSearch(URLEncode(q.toString()), fuzzyMode)
    }

    override fun uniqueAlbums(tracks: List<Track>): Set<Track.Album> {
        val albums = mutableSetOf<Track.Album>()
        tracks.forEach{albums.add(it.album)}
        return albums
    }

    override fun uniqueArtists(tracks: List<Track>): Set<Track.Artist> {
        val artists = mutableSetOf<Track.Artist>()
        tracks.forEach{artists.add(it.artist)}
        return artists
    }

    /**
     * @param q url encoded query string
     */
    private fun apiSearch(q: String, fuzzyMode: Boolean): List<Track> {
        val strict = if (fuzzyMode) "off" else "on"
        val url = "$baseURL/search?q=$q&strict=$strict"
        // make API request and map result to SearchResults
        return JSONObject(content(url)).getJSONArray("data").map { Track(it) }
    }

    override fun getAlbumCover(albumId: Int, size: ImageSize): ImageAsset {
        // TODO improve by caching covers locally
        val url = "$baseURLAlbum/$albumId/image?size=${size.identifier}"
        return bitmap(url).asImageAsset()
    }

    override fun getArtistCover(artistId: Int, size: ImageSize): ImageAsset {
        val url = "$baseURLArtist/$artistId/image?size=${size.identifier}"
        return getImage(url)
    }

    private fun getImage(url: String): ImageAsset {
        // TODO improve by caching covers locally
        return bitmap(url).asImageAsset()
    }
}