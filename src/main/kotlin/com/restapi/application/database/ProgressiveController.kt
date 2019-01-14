package com.restapi.application.database

import com.restapi.application.devices.Devices
import com.restapi.application.metadata.Metadata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Controller
import java.io.File
import org.apache.commons.io.IOUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.io.IOException
import java.util.Properties
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This controller handles incoming http requests and "ApplicationReady" events for loading progressive images
 *
 * @author      Markus Graf
 * @see         com.restapi.application.devices.Devices
 * @see         com.restapi.application.metadata.Metadata
 * @see         org.slf4j.LoggerFactory
 * @see         org.springframework.beans.factory.annotation.Autowired
 * @see         org.springframework.boot.context.event.ApplicationReadyEvent
 * @see         org.springframework.context.event.EventListener
 * @see         org.springframework.stereotype.Controller
 * @see         java.io.File
 * @see         org.apache.commons.io.IOUtils
 * @see         org.springframework.web.bind.annotation.PathVariable
 * @see         org.springframework.web.bind.annotation.RequestMapping
 * @see         org.springframework.web.bind.annotation.RequestMethod
 * @see         java.io.IOException
 * @see         java.util.java.util.Properties
 * @see         javax.servlet.http.HttpServletRequest
 * @see         javax.servlet.http.HttpServletResponse
 * @version     1.0
 */

@Controller
@RequestMapping(path=["/progressive"])
class ProgressiveController {
    private val logger = LoggerFactory.getLogger(ProgressiveController::class.java)

    @Autowired
    private var progressiveRepository: ProgressiveRepository? = null
    
    /**
     * Adds all images in the resource folder "images/progressive" to the database when the "ApplicationReady" event is
     * thrown
     *
     * @exception  IOException  if the given path doesn't include a legit
     */
    @Throws(IOException::class)
    @EventListener(ApplicationReadyEvent::class)
    fun addAllProgressiveImagesOnStartupToDatabase() {
        val pathOfCurrentDirectory = System.getProperty("user.dir")
        val pathToProgressiveImages = "$pathOfCurrentDirectory\\src\\main\\resources\\images\\progressive\\"
    
        progressiveRepository!!.deleteAll()
        logger.info("Load progressive images initially to database")

        File(pathToProgressiveImages).listFiles().forEach {
            if (it.extension == "jpeg" || it.extension == "jpg") {
                addNewImageToDatabase(file = it)
            }
        }

        logger.info("All images in folder 'images/progressive' added to database")
    }
    
    /**
     * Returns the requested JPEG image as byte array over the output stream of the response object.
     *
     * The desired JPEG image is requested by calling the url "localhost:8080/loadImage" or "127.0.0.1:8080/loadImage",
     * followed by the <code>fileName</code> of the JPEG file and the <code>width</code> of the device display
     * (in pixel) that sends the http request. Depending on the device display, bytes at the end of the image byte
     * array will be dumped before the array is sent over the output stream. Due to the progressive file format, the
     * JPEG file can still be displayed (the quality depends on the amount of dumped bytes). The JPEG file won't be
     * resized.
     *
     * @param       request     http request sent by browser
     * @param       response    response object provided by the http request
     * @param       fileName    filename of requested JPEG file
     * @param       width       display width of the device that sent the http request
     * @exception   IOException if the file doesn't exist
     * @return      progressive image over the output stream of the response object
     */
    @Throws(IOException::class)
    @RequestMapping(value = ["/loadImage/{fileName}/{width}"], method = [RequestMethod.GET])
    fun loadImage(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @PathVariable("fileName") fileName: String,
        @PathVariable("width") width: String
    ) {
        val images = progressiveRepository!!.findAll()
        var media: ByteArray? = null
    
        val deviceType = setMediaQueryRange(width = width)

        try {
            images.forEach {
                if (it.getName() == "${fileName.split(".").first()}.jpg" ||
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
            logger.warn("Progressive Image '$fileName' not found! Load first imafe available")
        }

        media = refactorByteArray(
                media = media!!,
                deviceType = deviceType
        )
        
        return response.outputStream.write(media)
    }
    
    /**
     * Depending on the requesting device type (Mobile, Tablet. Desktop), bytes at the end of the image byte array
     * will be dumped or not. Returns a new byte array with the remaining bytes of the JPEG image. The scale factor of
     * the remaining bytes in the image array can be be configured in the "device.properties" file in the resource
     * folder.
     *
     * @param   media     image byte array
     * @return  new image byte array optimized for the appropriate device type
     */
    private fun refactorByteArray(media: ByteArray, deviceType: Devices): ByteArray {
        val propertiesDevice = loadProperties()

        val size: Double = when (deviceType) {
            Devices.Mobile -> propertiesDevice.getProperty("mobile.image.load.scale").toDouble()
            Devices.Tablet -> propertiesDevice.getProperty("tablet.image.load.scale").toDouble()
            Devices.Desktop -> propertiesDevice.getProperty("desktop.image.load.scale").toDouble()
        }

        logger.info("Image load scale: $size")
        val newByteArraySize = (media.size.toDouble() * size).toInt()
        return media.copyOfRange(0, newByteArraySize)
    }
    
    /**
     * Creates a file of the given JPEG filename and returns the file as byte array
     *
     * @param       fileName     JPEG file name
     * @return      byte array of the JPEG file
     * @exception   IOException if the file doesn't exist
     * @see         javax.imageio.ImageIO#toByteArray(final InputStream input)
     */
    @Throws(IOException::class)
    private fun loadImage(fileName: String): ByteArray {
        val inputStream = File(fileName).inputStream()
        return IOUtils.toByteArray(inputStream)
    }
    
    /**
     * Loads all device properties from file "device.properties" in the resource folder
     *
     * @return  device properties
     */
    private fun loadProperties(): Properties {
        val propertiesPath = "device.properties"
        val propertiesFile = File(ProgressiveController::class.java.classLoader.getResource(propertiesPath).toURI())
        val propertiesDevice = Properties()

        propertiesDevice.load(propertiesFile.inputStream())
        return propertiesDevice
    }
    
    /**
     * Depending on the pre-configured device sizes in the file "device.properties" in the resource folder, the
     * device type will be defined with the given <code>width</code> of the device display (in pixel)
     *
     * @param   width   device's display width (in pixel)
     * @return  device type (Mobile, Tablet, Desktop)
     */
    private fun setMediaQueryRange(width: String): Devices {
        val propertiesDevice = loadProperties()

        val deviceType = if (width.toInt() <= propertiesDevice.getProperty("mobile.display.width").toInt()) {
            Devices.Mobile
        } else if (width.toInt() <= propertiesDevice.getProperty("tablet.display.width").toInt()) {
            Devices.Tablet
        } else {
            Devices.Desktop
        }

        logger.info("Device type '$deviceType' detected")
        return deviceType
    }
    
    /**
     * Adds a given JPEG file with its metadata to the database
     *
     * @param   file   JPEG file to be added to the database
     */
    private fun addNewImageToDatabase(file: File) {
        val progressive = Progressive()

        progressive.setName(file.name)
        progressive.setExtension(file.extension)
        progressive.setHeight(Metadata().getHeight(file = file))
        progressive.setWidth(Metadata().getWidth(file = file))
        progressive.setSize(file.readBytes().size)
        progressive.setPath(file.path)

        progressiveRepository!!.save(progressive)
        logger.info("Progressive image '${file.name}' saved in database")
    }
}