package hu.bme.vik.plugins

import hu.bme.vik.Config
import hu.bme.vik.clients.AiClient
import hu.bme.vik.model.Detections
import hu.bme.vik.model.Notification
import hu.bme.vik.repository.PictureRepository
import hu.bme.vik.utils.*
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
import kotlinx.coroutines.async
import org.koin.ktor.ext.inject
import pl.jutupe.ktor_rabbitmq.RabbitMQInstance
import pl.jutupe.ktor_rabbitmq.publish
import java.lang.Math.random
import java.time.Instant

fun Application.configureRouting() {
    val repository by inject<PictureRepository>()
    val rabbitMQInstance by inject<RabbitMQInstance>()

    install(ContentNegotiation) {
        json()
    }
    routing {
        staticResources("/static", "files")
        get("/") {
            call.respondRedirect("list")
        }
        get("list") {
            val posts = repository.getAllPicture()
            println(posts.size)

            call.respond(
                FreeMarkerContent("index.ftl", model =
                    mapOf("posts" to posts)
                )
            )
        }
        get("manualSend") {
            rabbitMQInstance.publish(
                Config.rabbitExchange,
                Config.rabbitRoutingKey,
                null,
                Notification("test desc", (random()*100).toInt(), Instant.now().format())
            )
        }
        get("download/{id}") {
            val id = call.parameters["id"]!!
            call.respondBytes(repository.getPicture(id), ContentType.Image.JPEG)
        }
        route("detector") {
            get {
                call.respond(FreeMarkerContent("upload.ftl", model = loadTemplateData()))
            }
            post {
                val multipartData = call.receiveMultipart()
                var fileBytes: ByteArray? = null
                var description: String? = null

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileBytes = part.streamProvider().readBytes()
                        }
                        is PartData.FormItem -> {
                            when (part.name) {
                                "description" -> { description = part.value }
                                else -> {}
                            }
                        }
                        is PartData.BinaryItem -> {}
                        is PartData.BinaryChannelItem -> {}
                    }
                }

                if (fileBytes == null || description == null) {
                    call.respond(FreeMarkerContent("error.ftl", model = mapOf("error" to "Missing image or description")))
                }

                val detectionRequest = async { AiClient.getDetection(fileBytes!!) }

                val bufferedImage = fileBytes!!.toBufferedImage()
                repository.upload(bufferedImage, description!!)

                val detectionResponse = detectionRequest.await()
                if (detectionResponse.status.value != 200) {
                    call.respond(FreeMarkerContent("error.ftl", model = mapOf("error" to detectionResponse.body<String>())))
                }

                val detections = detectionResponse.body<Detections>()
                rabbitMQInstance.publish(
                    Config.rabbitExchange,
                    Config.rabbitRoutingKey,
                    null,
                    Notification(description!!, detections.detections.size, Instant.now().format())
                )

                drawRectanglesForDetectedObjects(bufferedImage, detections.detections)

                val result = bufferedImage.toByteArray().toBase64()

                call.respond(FreeMarkerContent("upload.ftl", model = loadTemplateData(result)))
            }
        }
    }
}
