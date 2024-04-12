package hu.bme.vik

import hu.bme.vik.plugins.*
import hu.bme.vik.repository.PictureRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Config.aiRoute = environment.config.property("application.aiRoute").getString()
    Config.operatorJoinUrl = environment.config.property("application.operatorJoinUrl").getString()
    val bucketName = environment.config.property("application.bucketName").getString()
    val mongoClient = KMongo.createClient(
        environment.config.property("application.connectionString").getString()
    )
    val database = mongoClient.getDatabase(
        environment.config.property("application.databaseName").getString()
    )


    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { PictureRepository(database, bucketName) }
            }
        )
    }

    configureRouting()
    configureTemplating()

    routing {
        get("/health") {
            call.respond("Up and running.")
        }
    }
}
