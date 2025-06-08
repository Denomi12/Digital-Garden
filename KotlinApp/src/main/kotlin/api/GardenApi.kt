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
import models.Garden

object GardenApi {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val JSON_MEDIA_TYPE = "application/json".toMediaType()

    suspend fun getGardens(): List<Garden> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://localhost:3001/garden")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch gardens: ${response.code} - ${response.message}")
            }
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            json.decodeFromString<List<Garden>>(responseBody)
        }
    }

    suspend fun updateGarden(garden: Garden): Boolean = withContext(Dispatchers.IO) {
        val requestBody = json.encodeToString(garden).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("http://localhost:3001/garden/${garden._id}")
            .put(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No error body"
                throw Exception("Failed to update garden: ${response.code} - $errorBody")
            }
            true
        }
    }

    suspend fun createGarden(garden: Garden): Garden = withContext(Dispatchers.IO) {
        val requestBody = json.encodeToString(garden).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("http://localhost:3001/garden")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No error body"
                throw Exception("Failed to create garden: ${response.code} - $errorBody")
            }
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            json.decodeFromString<Garden>(responseBody)
        }
    }
}