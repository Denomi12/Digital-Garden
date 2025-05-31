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
import models.User

object UserApi {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val JSON_MEDIA_TYPE = "application/json".toMediaType()

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://localhost:3001/user")
            .build()

        // naredimo nov klic z built requestom od prej
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch users: ${response.code}")
            }
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            json.decodeFromString<List<User>>(responseBody)
        }
    }

    suspend fun updateUser(user: User): Boolean = withContext(Dispatchers.IO) {
        val requestBody = json.encodeToString(user).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("http://localhost:3001/user/${user._id}")
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            response.isSuccessful
        }
    }

    suspend fun createUser(user: User): Boolean = withContext(Dispatchers.IO) {
        val requestBody = json.encodeToString(user).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("http://localhost:3001/user")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            response.isSuccessful
        }
    }
}