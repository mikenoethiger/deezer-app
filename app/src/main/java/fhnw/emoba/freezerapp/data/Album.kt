package fhnw.emoba.freezerapp.data

import org.json.JSONArray
import org.json.JSONObject

class Album(albumObject: JSONObject) {
    // Structure described at https://developers.deezer.com/api/album
    val id = albumObject.getInt("id") // The Deezer album id
    val title = albumObject.getString("title") // The album title
    val upc = albumObject.getString("upc") // The album UPC
    val link = albumObject.getString("link") // The url of the album on Deezer
    val share = albumObject.getString("share") // The share link of the album on Deezer
    val cover = albumObject.getString("cover") // The url of the album's cover. Add 'size' parameter to the url to change size. Can be 'small', 'medium', 'big', 'xl'
    val md5Image = albumObject.getString("md5_image")
    val genreId = albumObject.getInt("genre_id") // The album's first genre id (You should use the genre list instead). NB : -1 for not found
    val genres = getGenres(albumObject.getJSONObject("genres").getJSONArray("data"))
    val label = albumObject.getString("label") // The album's label name
    val nbTracks = albumObject.getInt("nb_tracks") // Number of tracks
    val duration = albumObject.getInt("duration") // The album's duration (seconds)
    val fans = albumObject.getInt("fans") // The number of album's Fans
    val rating = albumObject.getInt("rating") // The album's rate
    val releaseDate = albumObject.getString("release_date") // The album's release date
    val recordType = albumObject.getString("record_type") // The record type of the album (EP / ALBUM / etc..)
    val available = albumObject.getBoolean("available")
    val tracklist = albumObject.getString("tracklist") // API Link to the tracklist of this album
    val artist = Artist(albumObject.getJSONObject("artist")) // artist object containing : id, name, picture, picture_small, picture_medium, picture_big, picture_xl
    val tracks = getAlbumTracks(albumObject.getJSONObject("tracks").getJSONArray("data"))

    private fun getGenres(genreArray: JSONArray): List<Genre> {
        val genres = mutableListOf<Genre>()
        for (i in 0 until genreArray.length()) {
            genres.add(Genre(genreArray.getJSONObject(i)))
        }
        return genres;
    }

    private fun getAlbumTracks(albumTracksArray: JSONArray): List<AlbumTrack> {
        val albumTracks = mutableListOf<AlbumTrack>()
        for (i in 0 until albumTracksArray.length()) {
            albumTracks.add(AlbumTrack(albumTracksArray.getJSONObject(i)))
        }
        return albumTracks;
    }
}