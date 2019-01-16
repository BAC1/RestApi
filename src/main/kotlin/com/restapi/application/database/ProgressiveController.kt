package com.restapi.application.database

import com.restapi.application.metadata.MediaQuery
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
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This controller handles incoming http requests and "ApplicationReady" events for loading progressive images.
 *
 * @author      Markus Graf
 * @see         com.restapi.application.metadata.MediaQuery
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
 * @see 		java.lang.Exception
 * @see         javax.servlet.http.HttpServletRequest
 * @see         javax.servlet.http.HttpServletResponse
 */
@Controller
@RequestMapping(path = ["/progressive"])
class ProgressiveController {
    private val logger = LoggerFactory.getLogger(ProgressiveController::class.java)

    @Autowired
    private var progressiveRepository: ProgressiveRepository? = null
    
    /**
     * Adds all images in the resource folder "images/progressive" to the database when the "ApplicationReady" event is
     * thrown.
     */
    @Throws(IOException::class)
    @EventListener(ApplicationReadyEvent::class)
    fun addAllProgressiveImagesOnStartupToDatabase() {
        val pathOfCurrentDirectory = System.getProperty("user.dir")
        val pathToProgressiveImages = "$pathOfCurrentDirectory/src/main/resources/images/progressive/"
	
		logger.info("Add progressive JPEG's to database")
		
		try {
			progressiveRepository!!.deleteAll()
			
			File(pathToProgressiveImages).listFiles().forEach {
				if (it.extension == "jpeg" || it.extension == "jpg") {
					addNewImageToDatabase(file = it)
				} else {
					logger.warn("File '${it.name}' with unsupported extension detected!")
				}
			}
			
			logger.info("All JPEG's in folder 'images/progressive' added to database")
		} catch (e: IOException) {
			logger.error("Error while loading JPEG's to database!\n\t${e.message}")
		} catch (e: Exception) {
			logger.error(e.message)
		}
    }
    
    /**
     * Returns the requested JPEG image as byte array over the output stream of the response object.
     *
     * The desired JPEG image is requested by calling the url "localhost:8080/progressive/loadImage/" or
     * "127.0.0.1:8080/progressive/loadImage/", followed by the <code>fileName</code> of the JPEG file and the
     * <code>width</code> of the device display (in pixel) that sends the http request. Depending on the device display,
     * bytes at the end of the progressive-encoded image byte array will be dumped before the array is sent over the
     * output stream. Due to the progressive encoding, the JPEG file can still be displayed. The quality depends on the
     * amount of the dumped bytes.
     *
     * @param   request                 http request sent by browser
     * @param   response                response object provided by the http request
     * @param   fileName                filename of requested JPEG file
     * @param   width                   display width of the device that sent the http request
     * @throws  IOException             if e.g. <code>pathname</code> argument is <code>null</code>
     * @return  progressive image over the output stream of the response object
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
        var media = ByteArray(0)
        val deviceType = MediaQuery().get(width = width)
	
		logger.info("Url '//loadImage/$fileName/$width' requested")
	
		try {
            images.forEach {
                if (it.getName() == "${fileName.split(".").first()}.jpg" ||
                    it.getName() == "${fileName.split(".").first()}.jpeg"
                ) {
                    media = loadImage(it.getPath()!!)
                    logger.info("Progressive image '$fileName' is requested by browser")
					media = ProgressiveConfiguration().refactorByteArray(media = media, deviceType = deviceType)
				}
            }
        } catch (e: Exception) {
            logger.error("Error while loading image $fileName!\n\t${e.message}")
        }
		
        return response.outputStream.write(media)
    }
    
    /**
     * Creates a file of the given JPEG filename and returns it as byte array.
     *
     * @param   fileName                JPEG file name
     * @return  byte array of the JPEG file
     * @see     javax.imageio.ImageIO#toByteArray(final InputStream input)
     */
    private fun loadImage(fileName: String): ByteArray {
		return try {
			val inputStream = File(fileName).inputStream()
			IOUtils.toByteArray(inputStream)
		} catch (e: Exception) {
			logger.error("Error while creating byte array of input stream!\n\t${e.message}")
			ByteArray(0)
		}
    }
    
    /**
     * Adds the byte array of a given JPEG file with its metadata to the database.
     *
     * @param  file  JPEG file to be added to the database
     */
	private fun addNewImageToDatabase(file: File) {
		try {
			val progressive = Progressive()
			
			progressive.setName(file.name)
			progressive.setExtension(file.extension)
			progressive.setHeight(Metadata().getHeight(file = file))
			progressive.setWidth(Metadata().getWidth(file = file))
			progressive.setSize(file.readBytes().size)
			progressive.setPath(file.path)
			
			progressiveRepository!!.save(progressive)
			logger.info("Progressive image '${file.name}' added to database")
		} catch (e: Exception) {
			logger.error("Failed to add image '${file.name}' to database!")
		}
    }
}