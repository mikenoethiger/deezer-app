package fhnw.emoba.freezerapp.data

import org.json.JSONObject

class Artist(artistJSONObject: JSONObject) {
    // Artist API docs: https://developers.deezer.com/api/artist
    val id            = artistJSONObject.getInt("id") // The artist's Deezer id
    val name          = artistJSONObject.getString("name") // The artist's name
    val link          = artistJSONObject.getString("link") // The url of the artist on Deezer
    val share         = artistJSONObject.getString("share") // The share link of the artist on Deezer
    val picture       = artistJSONObject.getString("picture") // The url of the artist picture. Add 'size' parameter to the url to change size. Can be 'small', 'medium', 'big', 'xl'
    val pictureSmall  = artistJSONObject.getString("picture_small") // The url of the artist picture in size small.
    val pictureMedium = artistJSONObject.getString("picture_medium") // The url of the artist picture in size medium.
    val pictureBig    = artistJSONObject.getString("picture_big") // The url of the artist picture in size big.
    val pictureXL     = artistJSONObject.getString("picture_xl") // The url of the artist picture in size xl.
    val nbAlbum       = artistJSONObject.getInt("nb_album") // The number of artist's albums
    val nbFan         = artistJSONObject.getInt("nb_fan") // The number of artist's fans
    val radio         = artistJSONObject.getBoolean("radio") // true if the artist has a smartradio
    val tracklist     = artistJSONObject.getString("tracklist") // API Link to the top of this artist
}