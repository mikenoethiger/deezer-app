package fhnw.emoba.freezerapp.data.impl

import fhnw.emoba.freezerapp.data.*
import org.json.JSONObject
import java.lang.StringBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object RemoteDeezerService : DeezerService {
    private const val baseURL = "https://api.deezer.com"

    override fun search(searchParameters: Map<String, String>): List<SearchResult> {
        // API search docs: https://developers.deezer.com/api/search
        // Using advanced search here, example:
        // https://api.deezer.com/search?q=artist:%22eminem%22%20track:%22lose%20yourself%22

        // early return
        if (searchParameters.isEmpty()) return emptyList()
        // validation
        val validParams = arrayOf("artist", "album", "track", "label", "dur_min", "dur_max", "bpm_min", "bpm_max")
        searchParameters.keys.forEach { if (it !in validParams) error("Invalid parameter '$it'. Use any of: $validParams") }
        // build url
        val q = StringBuilder()
        searchParameters.forEach{ q.append("${it.key}:\"${it.value}\" ")}
        val url = "$baseURL/search?q=" + URLEncoder.encode(q.toString(), StandardCharsets.UTF_8.toString())
        // make API request and map result to SearchResults
        return JSONObject(content(url)).getJSONArray("data").map { SearchResult(it) }
    }
}