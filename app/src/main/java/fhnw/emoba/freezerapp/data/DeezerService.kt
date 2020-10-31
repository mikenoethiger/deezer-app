package fhnw.emoba.freezerapp.data

import androidx.compose.ui.graphics.ImageAsset

interface DeezerService {

    /**
     * Perform search for tracks as described at https://developers.deezer.com/api/search
     * @param query
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun search(query: String, fuzzyMode: Boolean = false): List<Track>
    /**
     * Perform extended search for tracks as described at https://developers.deezer.com/api/search
     * @param queryParameters keys must be any of [artist, album, track, label, dur_min, dur_max, bpm_min, bpm_max], values are the corresponding search terms
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun extendedSearch(queryParameters: Map<String, String>, fuzzyMode: Boolean = false): List<Track>

    /**
     * Get unique albums from a search result
     */
    fun uniqueAlbums(tracks: List<Track>): Set<Track.Album>

    /**
     * Get unique artists from a search result
     */
    fun uniqueArtists(tracks: List<Track>): Set<Track.Artist>

    fun getAlbumCover(albumId: Int, size: ImageSize = ImageSize.x400): ImageAsset

    fun getArtistCover(artistId: Int, size: ImageSize = ImageSize.x400): ImageAsset
}