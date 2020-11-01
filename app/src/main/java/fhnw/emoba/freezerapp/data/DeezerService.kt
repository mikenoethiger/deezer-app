package fhnw.emoba.freezerapp.data

import androidx.compose.ui.graphics.ImageAsset

interface DeezerService {

    /**
     * Perform search for tracks as described at https://developers.deezer.com/api/search
     * @param query
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun search(query: String, fuzzyMode: Boolean = false, order: SearchOrder = SearchOrder.RANKING): List<Track>

    /**
     * Perform extended search for tracks as described at https://developers.deezer.com/api/search
     * @param queryParameters keys must be any of [artist, album, track, label, dur_min, dur_max, bpm_min, bpm_max], values are the corresponding search terms
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun extendedSearch(
        queryParameters: Map<String, String>,
        fuzzyMode: Boolean = false,
        order: SearchOrder = SearchOrder.RANKING
    ): List<Track>

    /**
     * Load top tracks of the artist.
     * @param limit number of tracks to load
     * @param index start index of top tracks, 0 means load `limit` tracks beginning from the first top track
     */
    fun loadTopTracks(artist: Track.Artist, limit: Int = 10, index: Int = 0): List<Track>

    fun loadArtist(artistId: Int): Track.Artist

    fun loadAlbumCoversAsync(tracks: List<Track>)
    fun loadAlbumCoversAsync2(albums: List<Track.Album>)
    fun loadArtistCoversAsync(tracks: List<Track>)

    /**
     * Extract unique albums from a list of tracks
     */
    fun uniqueAlbums(tracks: List<Track>): Set<Track.Album>

    /**
     * Extract unique artists from a search result
     */
    fun uniqueArtists(tracks: List<Track>): Set<Track.Artist>
    fun uniqueContributors(tracks: List<Track>): Set<Track.Artist>

    fun getAlbumCover(albumId: Int, size: ImageSize = ImageSize.x400): ImageAsset

    fun getArtistCover(artistId: Int, size: ImageSize = ImageSize.x400): ImageAsset
}

enum class SearchOrder {
    RANKING,
    TRACK_ASC, TRACK_DESC,
    ARTIST_ASC, ARTIST_DESC,
    ALBUM_ASC, ALBUM_DESC,
    RATING_ASC, RATING_DESC,
    DURATION_ASC, DURATION_DESC
}