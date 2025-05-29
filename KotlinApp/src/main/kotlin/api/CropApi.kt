package api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import models.Crop

object CropApi {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val JSON_MEDIA_TYPE = "application/json".toMediaType()

    suspend fun getCrops(): List<Crop> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://localhost:3001/crop")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch crops: ${response.code} - ${response.message}")
            }
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            json.decodeFromString<List<Crop>>(responseBody)
        }
    }

    suspend fun updateCrop(crop: Crop): Boolean = withContext(Dispatchers.IO) {
        val requestBody = json.encodeToString(crop).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("http://localhost:3001/crop/${crop._id}")
            .put(requestBody)
            .addHeader("Content-Type", "application/json")  // Dodaj header
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No error body"
                throw Exception("Failed to update crop: ${response.code} - $errorBody")
            }
            true
        }
    }

}