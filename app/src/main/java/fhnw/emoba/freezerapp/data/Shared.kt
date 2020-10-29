package fhnw.emoba.freezerapp.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import java.net.URLEncoder

fun content(url: String) : String = content(streamFrom(url))

fun content(fileName: String, context: Context): String = content(context.assets.open(fileName))

fun content(inputStream: InputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    val jsonString = reader.readText()
    reader.close()
    
    return jsonString
}

fun JSONArrayFrom(fileName: String, context: Context) : JSONArray = JSONArray(content(fileName, context))

fun <T>dataListFrom(fileName: String, context: Context, transform: (JSONObject) -> T) : List<T> = JSONArray(content(fileName, context)).map(transform)

fun bitmap(url: String) = bitmap(streamFrom(url))

fun bitmap(inputStream: InputStream) : Bitmap {
    val bitmapAsBytes = inputStream.readBytes()
    inputStream.close()
    
    return BitmapFactory.decodeByteArray(bitmapAsBytes, 0, bitmapAsBytes.size)
}

fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T>{
    val list = mutableListOf<T>()
    for (i in 0 until length()) {
        list.add(transform(getJSONObject(i)))
    }
    return list
}

fun streamFrom(url: String): InputStream {
    val conn = URL(url).openConnection() as HttpsURLConnection
    conn.connect()
    
    return conn.inputStream
}

fun URLEncode(q: String) = URLEncoder.encode(q, StandardCharsets.UTF_8.toString())

fun defaultImage(size: Int): ImageAsset {
    val bitmap = Bitmap.createBitmap(
        size,
        size,
        Bitmap.Config.ALPHA_8
    )
    return bitmap.asImageAsset()
}

enum class ImageSize(val size: Int, val identifier: String, val defaultImage: ImageAsset = defaultImage(size)) {
    x50(50, "small"),
    x120(120, "medium"),
    x400(400, "big"),
    x1000(1000, "xl")
}

fun formatDuration(seconds: Int): String {
    val hours = seconds/60/60
    val mins = (seconds - hours*60*60)/60
    val secs = (seconds - hours*60*60 - mins*60)
    if (hours != 0) return hours.toString() + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs)
    return mins.toString() + ":" + String.format("%02d", secs)
}