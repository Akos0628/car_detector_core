package hu.bme.vik

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.vik.plugins.*
import hu.bme.vik.repository.PictureRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.litote.kmongo.reactivestreams.KMongo
import pl.jutupe.ktor_rabbitmq.RabbitMQ

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Config.aiRoute = environment.config.property("application.aiRoute").getString()
    Config.operatorJoinUrl = environment.config.property("application.operatorJoinUrl").getString()
    Config.rabbitUri = environment.config.property("application.rabbitmq.uri").getString()
    Config.rabbitConnectionName = environment.config.property("application.rabbitmq.connectionName").getString()
    Config.rabbitExchange = environment.config.property("application.rabbitmq.exchange").getString()
    Config.rabbitQueue = environment.config.property("application.rabbitmq.queue").getString()
    Config.rabbitRoutingKey = environment.config.property("application.rabbitmq.routingKey").getString()


    val bucketName = environment.config.property("application.bucketName").getString()
    val mongoClient = KMongo.createClient(
        environment.config.property("application.connectionString").getString()
    )
    val database = mongoClient.getDatabase(
        environment.config.property("application.databaseName").getString()
    )

    install(RabbitMQ) {
        uri = Config.rabbitUri
        connectionName = Config.rabbitConnectionName

        enableLogging()

        //serialize and deserialize functions are required
        serialize { jacksonObjectMapper().writeValueAsBytes(it) }
        deserialize { bytes, type -> jacksonObjectMapper().readValue(bytes, type.javaObjectType) }

        //example initialization logic (create queues etc.)
        initialize { // RabbitMQ Channel.() block ->
            exchangeDeclare(
                /* exchange = */ Config.rabbitExchange,
                /* type = */ "direct",
                /* durable = */ true
            )
            queueDeclare(
                /* queue = */ Config.rabbitQueue,
                /* durable = */true,
                /* exclusive = */false,
                /* autoDelete = */false,
                /* arguments = */emptyMap()
            )
        }
    }

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
