package hu.bme.vik.repository

import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.mongodb.reactivestreams.client.MongoDatabase
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import hu.bme.vik.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.bson.Document
import org.litote.kmongo.coroutine.coroutine
import org.reactivestreams.Publisher
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.UUID.randomUUID
import javax.imageio.ImageIO


class PictureRepository(
    database: MongoDatabase,
    bucketName: String,
) {
    private val bucket = GridFSBuckets.create(database, bucketName)

    suspend fun upload(bufferedImage: BufferedImage, description: String) {
        val options = GridFSUploadOptions()
            .metadata(Document("description", description))

        val outputStream = ByteArrayOutputStream()
        withContext(Dispatchers.IO) {
            ImageIO.write(bufferedImage, "jpeg", outputStream)
        }

        val byteBuffer = ByteBuffer.wrap(outputStream.toByteArray())

        val imageName = randomUUID().toString()
        val content: Publisher<ByteBuffer> = Publisher<ByteBuffer> {
            it.onNext(byteBuffer)
            it.onComplete()
        }
        bucket.uploadFromPublisher("$imageName.jpeg", content, options).coroutine.consumeEach { println(it) }
    }

    suspend fun getAllPicture() = bucket
        .find()
        .asFlow()
        .map {
            Post(
                "/download/" + it.filename.toString(),
                it.metadata!!["description"].toString()
            )
        }.toList()

    suspend fun getPicture(id: String): ByteArray {
        val byteBuffers: MutableList<ByteBuffer> = mutableListOf()
        bucket
            .downloadToPublisher(id)
            .coroutine
            .consumeEach {
                byteBuffers.add(it)
            }

        val byteArrayOutputStream = ByteArrayOutputStream()
        byteBuffers.forEach { byteArrayOutputStream.write(it.array()) }
        return byteArrayOutputStream.toByteArray()
    }
}