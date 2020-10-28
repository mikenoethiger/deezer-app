package fhnw.emoba.freezerapp.data.impl

import fhnw.emoba.freezerapp.data.*
import org.json.JSONObject
import java.lang.StringBuilder

object RemoteDeezerService : DeezerService {
    private const val baseURL = "https://api.deezer.com"

    override fun search(query: String, fuzzyMode: Boolean): List<SearchResult> {
        // early return
        if (query.isBlank()) return emptyList()
        val q = URLEncode("\"$query\"")
        return apiSearch(q, fuzzyMode)
    }

    override fun extendedSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean): List<SearchResult> {
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

    override fun uniqueAlbums(searchResults: List<SearchResult>): Set<SearchResult.Album> {
        val albums = mutableSetOf<SearchResult.Album>()
        searchResults.forEach{albums.add(it.album)}
        return albums
    }

    override fun uniqueArtists(searchResults: List<SearchResult>): Set<SearchResult.Artist> {
        val artists = mutableSetOf<SearchResult.Artist>()
        searchResults.forEach{artists.add(it.artist)}
        return artists
    }

    /**
     * @param q url encoded query string
     */
    private fun apiSearch(q: String, fuzzyMode: Boolean): List<SearchResult> {
        val strict = if (fuzzyMode) "off" else "on"
        val url = "$baseURL/search?q=$q&strict=$strict"
        // make API request and map result to SearchResults
        return JSONObject(content(url)).getJSONArray("data").map { SearchResult(it) }
    }
}