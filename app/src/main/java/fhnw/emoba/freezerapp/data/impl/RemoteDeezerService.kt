package fhnw.emoba.freezerapp.data.impl

import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import fhnw.emoba.freezerapp.data.*
import org.json.JSONObject
import java.util.*

object RemoteDeezerService : DeezerService {
    private const val baseURL = "https://api.deezer.com"
    private const val baseURLAlbum = "$baseURL/album"
    private const val baseURLArtist = "$baseURL/artist"
    private const val baseURLTrack = "$baseURL/track"
    private const val baseURLRadio = "$baseURL/radio"

    private val imageCache = Collections.synchronizedMap(LRUCache<String, ImageAsset>(300))

    override fun search(query: String, fuzzyMode: Boolean, order: SearchOrder): List<Track> {
        // early return
        if (query.isBlank()) return emptyList()
        val q = URLEncode("\"$query\"")
        return apiSearch(q, fuzzyMode, order)
    }

    override fun extendedSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean, order: SearchOrder): List<Track> {
        // early return
        if (queryParameters.isEmpty()) return emptyList()
        // validation
        val validParams = arrayOf("artist", "album", "track", "label", "dur_min", "dur_max", "bpm_min", "bpm_max")
        queryParameters.keys.forEach { if (it !in validParams) error("Invalid parameter '$it'. Use any of: $validParams") }
        // build url
        val q = StringBuilder()
        queryParameters.forEach{ q.append("${it.key}:\"${it.value}\" ")}
        return apiSearch(URLEncode(q.toString()), fuzzyMode, order)
    }

    override fun loadTracks(obj: HasTrackList): List<Track> {
        val response = JSONObject(content(obj.getTrackListUrl()))
        // some radios return an exception when trying to load the track list, such as:
        // https://api.deezer.com/radio/30771/tracks
        if (response.has("error")) return emptyList()
        return response.getJSONArray("data").map { Track(it) }
    }

    override fun loadTracks(obj: HasTrackList, limit: Int, index: Int): List<Track> {
        return JSONObject(content(obj.getTrackListUrl(limit, index))).getJSONArray("data").map { Track(it) }
    }

    override fun loadTrack(trackID: Int): Track = Track(content("$baseURLTrack/$trackID"))
    override fun loadArtist(artistId: Int): Artist = Artist(content("$baseURLArtist/$artistId"))
    override fun loadAlbum(albumId: Int): Album = Album(content("$baseURLAlbum/$albumId"))
    override fun loadRadios(): List<Radio> {
        return JSONObject(content(baseURLRadio)).getJSONArray("data").map { Radio(it) }
    }

    override fun lazyLoadImages(obj: HasImage) {
        if (obj.imagesLoaded) return
        if (obj.getImageUrl().isBlank()) return
        val x120URL = obj.getImageUrl(ImageSize.x120)
        val x400URL = obj.getImageUrl(ImageSize.x400)
        if (imageCache[x120URL] == null) imageCache[x120URL] = getImage(x120URL)
        if (imageCache[x400URL] == null) imageCache[x400URL] = getImage(x400URL)
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
    private fun apiSearch(q: String, fuzzyMode: Boolean, order: SearchOrder): List<Track> {
        val strict = if (fuzzyMode) "off" else "on"
        val url = "$baseURL/search?q=$q&strict=$strict&order=${order}"
        // make API request and map result to SearchResults
        return JSONObject(content(url)).getJSONArray("data").map { Track(it) }
    }

    private fun getImage(url: String): ImageAsset {
        // TODO improve by caching covers locally
        return bitmap(url).asImageAsset()
    }
}