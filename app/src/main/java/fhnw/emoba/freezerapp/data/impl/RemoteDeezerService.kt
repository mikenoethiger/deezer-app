package fhnw.emoba.freezerapp.data.impl

import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import fhnw.emoba.freezerapp.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.StringBuilder

object RemoteDeezerService : DeezerService {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private const val baseURL = "https://api.deezer.com"
    private const val baseURLAlbum = "$baseURL/album"
    private const val baseURLArtist = "$baseURL/artist"

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

    override fun loadTopTracks(artist: Track.Artist, limit: Int, index: Int): List<Track> {
        val url = artist.getTrackListLink(limit, index)
        return JSONObject(content(url)).getJSONArray("data").map { Track(it) }
    }

    override fun loadArtist(artistId: Int): Track.Artist {
        val url = "$baseURLArtist/$artistId"
        val json = JSONObject(content(url))
        return Track.Artist(json)
    }

    override fun loadAlbumCoversAsync(tracks: List<Track>) {
        serviceScope.launch {
            tracks.forEach {
                it.album.coverX400 = getAlbumCover(it.album.id, ImageSize.x400)
                it.album.coverX120 = getAlbumCover(it.album.id, ImageSize.x120)
            }
        }
    }

    override fun loadAlbumCoversAsync2(albums: List<Track.Album>) {
        serviceScope.launch {
            albums.forEach {
                it.coverX400 = getAlbumCover(it.id, ImageSize.x400)
                it.coverX120 = getAlbumCover(it.id, ImageSize.x120)
            }
        }
    }

    override fun loadArtistCoversAsync(tracks: List<Track>) {
        serviceScope.launch {
            tracks.forEach {
                it.artist.pictureX400 = getArtistCover(it.artist.id, ImageSize.x400)
            }
        }
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

    override fun uniqueContributors(tracks: List<Track>): Set<Track.Artist> {
        val contributors: MutableSet<Track.Artist> = mutableSetOf()
        tracks.forEach{ contributors.addAll(it.contributors) }
        return contributors.toSet()
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