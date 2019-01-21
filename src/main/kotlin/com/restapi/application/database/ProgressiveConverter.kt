package com.restapi.application.database

import org.slf4j.LoggerFactory
import javax.imageio.IIOImage
import javax.imageio.ImageWriteParam
import javax.imageio.ImageIO
import java.io.FileOutputStream
import java.io.File

class ProgressiveConverter {
	private val logger = LoggerFactory.getLogger(ProgressiveConverter::class.java)
	
	fun convertToProgressive(savePath: String, imageFile: File) {
		try {
			val bufferedImage = ImageIO.read(imageFile)
			val outputFile = File(savePath)
			val fileOutputStream = FileOutputStream(outputFile)
			val imageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
			val imageOutputStream = ImageIO.createImageOutputStream(fileOutputStream)
			
			imageWriter.output = imageOutputStream
			val param = imageWriter.defaultWriteParam
			param.progressiveMode = ImageWriteParam.MODE_EXPLICIT
			
			imageWriter.write(null, IIOImage(bufferedImage, null, null), param)
			
			fileOutputStream.close()
			imageOutputStream.close()
			imageWriter.dispose()
			
			logger.info("JPEG image '${imageFile.name}' converted to Progressive")
		} catch (e: Exception) {
			logger.error("Error while converting JPEG image '${imageFile.name}' to Progressive. " +
					"Aborting process ...\n${e.message}")
		}
	}
	
	fun moveToProgressiveFolder(imageFile: File, pathToProgressiveImages: String) {
		try {
			imageFile.copyTo(
					target = File("$pathToProgressiveImages/${imageFile.name}.${imageFile.extension}"),
					overwrite = true
			)
			
			imageFile.delete()
		} catch (e: Exception) {
			logger.error("Error while moving progressive image '${imageFile.name}'. Aborting process ...\n" +
					"${e.message}")
		}
	}
}