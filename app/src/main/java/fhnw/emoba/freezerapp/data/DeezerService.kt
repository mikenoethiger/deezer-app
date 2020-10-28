package fhnw.emoba.freezerapp.data

interface DeezerService {

    /**
     * Search for tracks
     * @param searchParameters keys must be any of [artist, album, track, label, dur_min, dur_max, bpm_min, bpm_max], values are the corresponding search terms
     */
    fun search(searchParameters: Map<String, String>): List<SearchResult>

}