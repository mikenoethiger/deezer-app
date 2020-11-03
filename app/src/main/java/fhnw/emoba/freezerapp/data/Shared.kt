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
import android.util.Log
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import java.net.URLEncoder
import java.text.NumberFormat

fun content(url: String) : String {
    Log.d("HTTP Request","GET $url")
    return content(streamFrom(url))
}

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

enum class DurationFormat {
    CLOCK,
    READABLE
}

/**
 * Format duration given in seconds
 * @param format 0=clock format (12:15:11), 1=readable format (12 hr. 15 min. 11 sec.)
 */
fun formatDuration(seconds: Int, format: DurationFormat = DurationFormat.CLOCK): String {
    val hr = seconds/60/60
    val min = (seconds - hr*60*60)/60
    val sec = (seconds - hr*60*60 - min*60)
    when (format) {
        DurationFormat.CLOCK -> {
            if (hr != 0) return hr.toString() + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec)
            return min.toString() + ":" + String.format("%02d", sec)
        }
        DurationFormat.READABLE -> {
            var duration = "$seconds sec."
            if (min != 0) duration = "$min min. $duration"
            if (hr != 0) duration = "$hr hr. $duration"
            return duration
        }
        else -> throw UnsupportedOperationException("format $format not yet implemented")
    }
}

fun formatNumber(n: Int): String = NumberFormat.getIntegerInstance().format(n)