package fhnw.emoba.freezerapp.data.impl

import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import fhnw.emoba.freezerapp.data.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object RemoteDeezerService : DeezerService {
    private const val baseURL = "https://api.deezer.com"
    private const val searchURL = "$baseURL/search"
    private const val searchArtistURL = "$searchURL/artist"
    private const val albumURL = "$baseURL/album"
    private const val artistURL = "$baseURL/artist"
    private const val trackURL = "$baseURL/track"
    private const val radioURL = "$baseURL/radio"

    private val imageCache = Collections.synchronizedMap(LRUCache<String, ImageAsset>(300))

    override fun searchTracks(query: String, fuzzyMode: Boolean, order: SearchOrder): List<Track> {
        if (query.isBlank()) return emptyList()
        val q = URLEncode("\"$query\"")
        return apiSearch(searchURL, q, fuzzyMode, order).map { Track(it) }
    }

    override fun searchArtists(query: String, fuzzyMode: Boolean, order: SearchOrder): List<Artist> {
        if (query.isBlank()) return emptyList()
        val q = URLEncode("\"$query\"")
        return apiSearch(searchArtistURL, q, fuzzyMode, order).map { Artist(it) }
    }

    override fun extendedTrackSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean, order: SearchOrder): List<Track> {
        // early return
        if (queryParameters.isEmpty()) return emptyList()
        // validation
        val validParams = arrayOf("artist", "album", "track", "label", "dur_min", "dur_max", "bpm_min", "bpm_max")
        queryParameters.keys.forEach { if (it !in validParams) error("Invalid parameter '$it'. Use any of: $validParams") }
        // build url
        val q = StringBuilder()
        queryParameters.forEach{ q.append("${it.key}:\"${it.value}\" ")}
        return apiSearch(searchURL, URLEncode(q.toString()), fuzzyMode, order).map { Track(it) }
    }

    override fun loadTrack(trackID: Int): Track = Track(content("$trackURL/$trackID"))
    override fun loadArtist(artistId: Int): Artist = Artist(content("$artistURL/$artistId"))
    override fun loadAlbum(albumId: Int): Album = Album(content("$albumURL/$albumId"))
    override fun loadRadios(): List<Radio> {
        return JSONObject(content(radioURL)).getJSONArray("data").map { Radio(it) }
    }

    override fun loadTracks(obj: HasTrackList): List<Track> {
        val response = JSONObject(content(obj.getTrackListUrl()))
        // some radios return an exception when trying to load the track list, such as:
        // https://api.deezer.com/radio/30771/tracks
        if (response.has("error")) return emptyList()
        return response.getJSONArray("data").map { Track(it) }
    }

    override fun loadTracks(obj: HasTrackList, limit: Int, index: Int): List<Track> {
        val response = JSONObject(content(obj.getTrackListUrl(limit, index)))
        if (response.has("error")) return emptyList()
        return response.getJSONArray("data").map { Track(it) }
    }

    override fun lazyLoadImages(obj: HasImage) {
        if (obj.imagesLoaded) return
        if (obj.getImageUrl().isBlank()) return
        val x120URL = obj.getImageUrl(ImageSize.x120)
        val x400URL = obj.getImageUrl(ImageSize.x400)
        if (imageCache[x120URL] == null) imageCache[x120URL] = bitmap(x120URL).asImageAsset()
        if (imageCache[x400URL] == null) imageCache[x400URL] = bitmap(x400URL).asImageAsset()
        obj.imageX120 = imageCache[x120URL]!!
        obj.imageX400 = imageCache[x400URL]!!
        obj.imagesLoaded = true
    }

    override fun uniqueAlbums(tracks: List<Track>): Set<Album> {
        val albums = mutableSetOf<Album>()
        tracks.forEach{albums.add(it.album)}
        return albums
    }

    override fun uniqueArtists(tracks: List<Track>): Set<Artist> {
        val artists = mutableSetOf<Artist>()
        tracks.forEach{artists.add(it.artist)}
        return artists
    }

    override fun uniqueContributors(tracks: List<Track>): Set<Artist> {
        val contributors: MutableSet<Artist> = mutableSetOf()
        tracks.forEach{ contributors.addAll(it.contributors) }
        return contributors.toSet()
    }

    /**
     * @param q url encoded query string
     */
    private fun apiSearch(baseURL: String, q: String, fuzzyMode: Boolean, order: SearchOrder): JSONArray {
        val strict = if (fuzzyMode) "off" else "on"
        val url = "$baseURL?q=$q&strict=$strict&order=${order}"
        // make API request and map result to SearchResults
        return JSONObject(content(url)).getJSONArray("data")
    }
}