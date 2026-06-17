package com.cervellopl.tagmyimg.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class PlantNetApiService(private val apiKey: String) {

    data class PlantResult(val label: String, val score: Float)

    suspend fun identify(context: Context, uri: Uri): List<PlantResult> = withContext(Dispatchers.IO) {
        val imageBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IOException("Cannot read image from URI")
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

        val boundary = "FormBoundary${System.currentTimeMillis()}"
        val endpoint = "https://my-api.plantnet.org/v2/identify/all" +
                "?api-key=$apiKey&lang=en&nb-results=5&include-related-images=false"

        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connectTimeout = 30_000
            readTimeout = 30_000
        }

        try {
            DataOutputStream(connection.outputStream).use { out ->
                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"images\"; filename=\"plant\"\r\n")
                out.writeBytes("Content-Type: $mimeType\r\n\r\n")
                out.write(imageBytes)
                out.writeBytes("\r\n")
                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"organs\"\r\n\r\n")
                out.writeBytes("auto")
                out.writeBytes("\r\n--$boundary--\r\n")
                out.flush()
            }

            val code = connection.responseCode
            if (code != 200) {
                val body = connection.errorStream?.use { it.bufferedReader().readText() } ?: ""
                throw IOException("PlantNet error $code: $body")
            }

            parseResponse(connection.inputStream.use { it.bufferedReader().readText() })
        } finally {
            connection.disconnect()
        }
    }

    private fun parseResponse(json: String): List<PlantResult> {
        val results = JSONObject(json).getJSONArray("results")
        return buildList {
            for (i in 0 until results.length()) {
                val result = results.getJSONObject(i)
                val score = result.getDouble("score").toFloat()
                val species = result.getJSONObject("species")
                val scientific = species.getString("scientificNameWithoutAuthor")
                val commonNames = species.optJSONArray("commonNames")
                val common = if (commonNames != null && commonNames.length() > 0)
                    commonNames.getString(0) else null
                val label = if (common != null) "$scientific ($common)" else scientific
                add(PlantResult(label = label, score = score))
            }
        }
    }
}
