package hu.bme.vik.clients

import hu.bme.vik.Config
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

object AiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getDetection(
        fileBytes: ByteArray
    ) = client.submitFormWithBinaryData(
        url = Config.aiRoute,
        formData = formData {
            append(
                "file",
                fileBytes,
                Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=fileName")
                    append(HttpHeaders.Accept, ContentType.Application.Json)
                }
            )
        }
    )
}