package fhnw.emoba.freezerapp.data

interface DeezerService {

    /**
     * Perform search for tracks as described at https://developers.deezer.com/api/search
     * @param query
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun search(query: String, fuzzyMode: Boolean = false): List<SearchResult>
    /**
     * Perform extended search for tracks as described at https://developers.deezer.com/api/search
     * @param queryParameters keys must be any of [artist, album, track, label, dur_min, dur_max, bpm_min, bpm_max], values are the corresponding search terms
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun extendedSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean = false): List<SearchResult>

    /**
     * Get unique albums from a search result
     */
    fun uniqueAlbums(searchResults: List<SearchResult>): Set<SearchResult.Album>

    /**
     * Get unique artists from a search result
     */
    fun uniqueArtists(searchResults: List<SearchResult>): Set<SearchResult.Artist>
}