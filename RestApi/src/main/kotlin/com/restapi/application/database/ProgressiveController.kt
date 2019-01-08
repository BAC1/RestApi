package com.restapi.application.database

import com.restapi.application.image.Metadata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Controller
import java.io.File
import org.apache.commons.io.IOUtils
import org.springframework.web.bind.annotation.*
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping(path=["/progressive"])
class ProgressiveController {
    private val logger = LoggerFactory.getLogger(ProgressiveController::class.java)
    private val pathOfCurrentDirectory = System.getProperty("user.dir")
    private val pathToProgressiveImages = "$pathOfCurrentDirectory\\src\\main\\resources\\images\\progressive\\"

    @Autowired
    private var progressiveRepository: ProgressiveRepository? = null

    @EventListener(ApplicationReadyEvent::class)
    fun addAllProgressiveImagesOnStartupToDatabase() {
        progressiveRepository!!.deleteAll()
        logger.info("Load progressive images initially to database")

        File(pathToProgressiveImages).listFiles().forEach {
            if (it.extension == "jpeg" || it.extension == "jpg") {
                addNewImageToDatabase(file = it)
            }
        }
    }

    @Throws(IOException::class)
    @RequestMapping(value = ["/loadImage/{fileName}/{width}/{height}"], method = [RequestMethod.GET])
    fun loadImage(
            request: HttpServletRequest,
            response: HttpServletResponse,
            @PathVariable("fileName") fileName: String,
            @PathVariable("width") width: String,
            @PathVariable("height") height: String
    ) {
        val images = progressiveRepository!!.findAll()
        var media: ByteArray? = null

        setMediaQueryRange(
            width = width,
            height = height
        )

        try {
            images.forEach {
                if (
                    it.getName() == "${fileName.split(".").first()}.jpg" ||
                    it.getName() == "${fileName.split(".").first()}.jpeg"
                ) {
                    media = loadImage(it.getPath()!!)
                    logger.info("Return progressive image '$fileName' to browser")
                }
            }
        } catch (e: Exception) {
            logger.error(e.message.toString())
        }

        if (media == null) {
            media = loadImage(images.first().getPath()!!)
            logger.warn("Progressive image '$fileName' not found! Load first image available")
        }

        media = sizeByteArrayForDevice(media!!)
        return response.outputStream.write(media)
    }

    private fun sizeByteArrayForDevice(media: ByteArray): ByteArray {
        val size: Double = when (deviceType) {
            Device.Mobile -> 0.6
            Device.Tablet -> 0.8
            Device.Desktop -> 1.0
            else -> {
                logger.warn("Unknown device type '$deviceType' found!")
                1.0
            }
        }

        val newByteArraySize = (media.size.toDouble() * size).toInt()
        return media.copyOfRange(0, newByteArraySize)
    }

    private fun loadImage(fileName: String): ByteArray {
        val inputStream = File(fileName).inputStream()
        return IOUtils.toByteArray(inputStream)
    }

    private fun setMediaQueryRange(width: String, height: String) {
        deviceType = if (width.toInt() < 500 && height.toInt() < 900) {
            Device.Mobile
        } else if (width.toInt() <= 1024 && height.toInt() <= 1366) {
            Device.Tablet
        } else {
            Device.Desktop
        }

        logger.info("Device type '$deviceType' detected")
    }

    private fun addNewImageToDatabase(file: File) {
        val progressive = Progressive()
        val metaData = Metadata(file)

        progressive.setName(file.name)
        progressive.setExtension(file.extension)
        progressive.setHeight(metaData.getHeight())
        progressive.setWidth(metaData.getWidth())
        progressive.setSize(file.readBytes().size)
        progressive.setPath(file.path)

        progressiveRepository!!.save(progressive)
        logger.info("Progressive image '${file.name}' saved in database")
    }

    private var deviceType: Device? = null

    private enum class Device {
        Mobile, Tablet, Desktop
    }
}