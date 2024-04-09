package hu.bme.vik

import hu.bme.vik.plugins.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Config.aiRoute = environment.config.property("application.aiRoute").getString()
    Config.operatorJoinUrl = environment.config.property("application.operatorJoinUrl").getString()

    configureRouting()
    configureTemplating()

    routing {
        get("/health") {
            call.respond("Up and running.")
        }
    }
}
