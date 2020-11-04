package fhnw.emoba.freezerapp.data

interface DeezerService {

    /**
     * Perform search for tracks as described at https://developers.deezer.com/api/search
     * @param query
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun searchTracks(query: String, fuzzyMode: Boolean = true, order: SearchOrder = SearchOrder.RANKING): List<Track>

    /**
     * Perform search for artists as described at https://developers.deezer.com/api/search/artist
     * @param query
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more artists are likely to match the query
     */
    fun searchArtists(query: String, fuzzyMode: Boolean = true, order: SearchOrder = SearchOrder.RANKING): List<Artist>

    /**
     * Perform extended search for tracks as described at https://developers.deezer.com/api/search
     * @param queryParameters keys must be any of [artist, album, track, label, dur_min, dur_max, bpm_min, bpm_max], values are the corresponding search terms
     * @param fuzzyMode weaker search when fuzzy mode is enabled, i.e. more tracks are likely to match the query
     */
    fun extendedTrackSearch(
        queryParameters: Map<String, String>,
        fuzzyMode: Boolean = false,
        order: SearchOrder = SearchOrder.RANKING
    ): List<Track>

    fun loadTracks(obj: HasTrackList): List<Track>
    fun loadTracks(obj: HasTrackList, limit: Int, index: Int = 0): List<Track>
    fun loadTrack(trackID: Int): Track

    fun loadArtist(artistId: Int): Artist

    fun loadAlbum(albumId: Int): Album

    fun loadRadios(): List<Radio>

    fun lazyLoadImages(obj: HasImage)

    /**
     * Extract unique albums from a list of tracks
     */
    fun uniqueAlbums(tracks: List<Track>): Set<Album>

    /**
     * Extract unique artists from a track list
     */
    fun uniqueArtists(tracks: List<Track>): Set<Artist>
    fun uniqueContributors(tracks: List<Track>): Set<Artist>
}

enum class SearchOrder {
    RANKING,
    TRACK_ASC, TRACK_DESC,
    ARTIST_ASC, ARTIST_DESC,
    ALBUM_ASC, ALBUM_DESC,
    RATING_ASC, RATING_DESC,
    DURATION_ASC, DURATION_DESC
}