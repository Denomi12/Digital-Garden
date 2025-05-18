package api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import models.User

object UserApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { // contentNegotiation za handlanje JSONa
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getUsers(): List<User> {
        return try {
            client.get("http://localhost:3001/user").body()
        } catch (e: Exception) {
            println("Napaka pri pridobivanju uporabnikov: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            client.put("http://localhost:3001/user/${user._id}") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(user)
            }
            true
        } catch (e: Exception) {
            println("Napaka pri posodabljanju uporabnika: ${e.message}")
            false
        }
    }

}
