package fhnw.emoba.freezerapp.data

import org.json.JSONObject

class Radio(json: JSONObject) : HasImage(), HasTrackList {

    val id = json.getInt("id")
    val title = json.getString("title")
    val picture = json.getString("picture")
    val tracklist = json.getString("tracklist")

    override fun getImageUrl(): String = picture
    override fun getTrackListUrl(): String = tracklist

    constructor(json: String): this(JSONObject(json))
}

val NULL_RADIO = Radio("{\"id\":-1,\"title\":\"\",\"description\":\"\",\"share\":\"\",\"picture\":\"\",\"tracklist\":\"\",\"md5_image\":\"\",\"type\":\"radio\"}")