package hu.bme.vik.utils

import hu.bme.vik.Config
import hu.bme.vik.model.Detected
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO


fun ByteArray.toBufferedImage(): BufferedImage {
    val bais = ByteArrayInputStream(this)
    try {
        return ImageIO.read(bais)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

fun BufferedImage.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    ImageIO.write(this, "JPEG", byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun drawRectanglesForDetectedObjects(
    bufferedImage: BufferedImage,
    detectedObjects: List<Detected>,
    shouldDrawText: Boolean = false
) {
    detectedObjects.forEach {
        println("Found ${it.label} with probability ${it.probability}")

        val top = it.yMin * bufferedImage.height
        val left = it.xMin * bufferedImage.width
        val bottom = it.yMax * bufferedImage.height
        val right = it.xMax * bufferedImage.width

        val graphics = bufferedImage.graphics as Graphics2D

        if (shouldDrawText) {
            graphics.color = Color.ORANGE
            graphics.font = Font("Courier New", 1, 12)
            graphics.drawString(" ${it.label}: ${String.format("%.3f", it.probability)}", left.toInt()-8, top.toInt()-4)
        }

        val stroke1: Stroke = BasicStroke(4f)
        when (it.label) {
            "car" -> graphics.color = Color.GREEN
            "person" -> graphics.color = Color.RED
            "bicycle" -> graphics.color = Color.BLUE
            else -> graphics.color = Color.MAGENTA
        }
        graphics.stroke = stroke1
        graphics.drawRect(left.toInt(), top.toInt(), (right - left).toInt(), (bottom - top).toInt())
    }
}

fun ByteArray.toBase64(): String =
    String(Base64.getEncoder().encode(this))

fun loadTemplateData(detectedImage: String? = null): Map<String, String> = mapOf(
    "operatorJoinUrl" to Config.operatorJoinUrl,
    "detectedImage" to if (detectedImage == null) "" else "data:image/png;base64, $detectedImage"
)
