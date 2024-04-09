package hu.bme.vik.plugins

import hu.bme.vik.Config
import hu.bme.vik.clients.AiClient
import hu.bme.vik.model.Detections
import hu.bme.vik.utils.drawRectanglesForDetectedObjects
import hu.bme.vik.utils.toBufferedImage
import hu.bme.vik.utils.toByteArray
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Base64

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        staticResources("/static", "files")
        get("/") {
            call.respondRedirect("detector")
        }
        route("detector") {
            get {
                call.respond(FreeMarkerContent("index.ftl", model = loadTemplateData()))
            }
            post {
                val multipartData = call.receiveMultipart()
                var fileBytes: ByteArray? = null

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileBytes = part.streamProvider().readBytes()
                        }
                        is PartData.FormItem -> {}
                        is PartData.BinaryItem -> {}
                        is PartData.BinaryChannelItem -> {}
                    }
                }

                if (fileBytes == null) {
                    val text = "No image given"
                    call.respondText(text, status = HttpStatusCode(400, text))
                }

                val detectionResponse = AiClient.getDetection(fileBytes!!)

                if (detectionResponse.status.value == 400) {
                    val text = "Bad request"
                    call.respondText(text, status = HttpStatusCode(400, text))
                }
                val detections = detectionResponse.body<Detections>()


                val bufferedImage = fileBytes!!.toBufferedImage()

                println(detections)
                drawRectanglesForDetectedObjects(bufferedImage, detections.detections)

                val result = bufferedImage.toByteArray().toBase64()

                call.respond(FreeMarkerContent("index.ftl", model = loadTemplateData(result)))
            }
        }
    }
}

fun ByteArray.toBase64(): String =
    String(Base64.getEncoder().encode(this))

fun loadTemplateData(detectedImage: String? = null): Map<String, String> = mapOf(
    "operatorJoinUrl" to Config.operatorJoinUrl,
    "detectedImage" to if (detectedImage == null) "" else "data:image/png;base64, $detectedImage"
)
