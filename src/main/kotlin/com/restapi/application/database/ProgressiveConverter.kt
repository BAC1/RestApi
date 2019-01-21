package com.restapi.application.database

import org.slf4j.LoggerFactory
import javax.imageio.IIOImage
import javax.imageio.ImageWriteParam
import javax.imageio.ImageIO
import java.io.FileOutputStream
import java.io.File
import java.io.IOException

/**
 * This class converts any JPG image in the resource folder "images/basement" to progressive and moves it to the
 * folder "images/progressive".
 *
 * @author      Markus Graf
 * @see 		org.slf4j.LoggerFactory
 * @see 		javax.imageio.IIOImage
 * @see 		javax.imageio.ImageWriteParam
 * @see 		javax.imageio.ImageIO
 * @see 		java.io.FileOutputStream
 * @see 		java.io.File
 * @see 		java.io.IOException
 */
class ProgressiveConverter {
	private val logger = LoggerFactory.getLogger(ProgressiveConverter::class.java)

	/**
	 * Converts any JPEG image in the resource folder "images/basement" to progressive
	 *
	 * @param   savePath		path where the new progressive-encoded shall be saved
	 * @param   imageFile		JPEG file to be converted to progressive-format
	 * @throws  IOException		if e.g. <code>pathname</code> argument is <code>null</code>
	 */
	@Throws(IOException::class)
	fun convertToProgressive(savePath: String, imageFile: File) {
		try {
			if (!imageFile.exists()) {
				 return logger.error("JPEG '${imageFile.name}' doesn't exist!")
			}

			val bufferedImage = ImageIO.read(imageFile)
			val outputFile = File(savePath)
			val fileOutputStream = FileOutputStream(outputFile)
			val imageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
			val imageOutputStream = ImageIO.createImageOutputStream(fileOutputStream)
			
			imageWriter.output = imageOutputStream
			val param = imageWriter.defaultWriteParam
			param.progressiveMode = ImageWriteParam.MODE_DEFAULT

			imageWriter.write(null, IIOImage(bufferedImage, null, null), param)
			
			fileOutputStream.close()
			imageOutputStream.close()
			imageWriter.dispose()
			deleteOldImage(imageFile)

			logger.info("JPEG image '${imageFile.name}' converted to Progressive")
		} catch (e: Exception) {
			logger.error("Error while converting JPEG image '${imageFile.name}' to Progressive. " +
					"Aborting process ...\n${e.message}")
		}
	}

	/**
	 * Deletes the non-encoded JPEG after the progression-encoding process
	 *
	 * @param   file			file to be deleted
	 */
	private fun deleteOldImage(file: File) {
		try {
			if (file.exists()) {
				file.delete()
			}
		} catch (e: Exception) {
			logger.error("Error while deleting non-encoded JPEG file!\n${e.message}")
		}
	}
}