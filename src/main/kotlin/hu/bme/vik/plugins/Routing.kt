package hu.bme.vik.plugins

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val client = HttpClient(CIO)

fun Application.configureRouting() {
    routing {
        staticResources("/static", "files")
        get("/") {
            call.respondRedirect("detector")
        }
        route("detector") {
            get {
                call.respond(FreeMarkerContent("index.ftl", model = null))
            }
        }
        get("/test") {
            var available = 0

            if (client.request("http://car-detector-ai:5000").status.value == 200) {
                available += 1
            }

            if (client.request("http://car-detector-notifier:5000").status.value == 200) {
                available += 1
            }

            call.respond(available)
        }
    }
}
