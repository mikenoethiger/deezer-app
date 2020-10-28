package fhnw.emoba.freezerapp.data

import org.json.JSONObject

class Genre(genreObject: JSONObject) {
    val id      = genreObject.getInt("id") // The editorial's Deezer id
    val name    = genreObject.getString("name") // The editorial's name
    val picture = genreObject.getString("picture") // The url of the genre picture. Add 'size' parameter to the url to change size. Can be 'small', 'medium', 'big', 'xl'
}