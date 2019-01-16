package com.restapi.application.database

import com.restapi.application.devices.Devices
import org.slf4j.LoggerFactory
import java.io.IOException
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * This class returns a re-sized byte array of the progressive-encoded JPEG.
 *
 * @author      Markus Graf
 * @see 		com.restapi.application.devices.Devices
 * @see 		org.slf4j.LoggerFactory
 * @see 		java.io.IOException
 * @see 		org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
class ProgressiveConfiguration {
	private val logger = LoggerFactory.getLogger(ProgressiveConfiguration::class.java)

	/**
	 * Depending on the requesting device type (Mobile, Tablet. Desktop), bytes at the end of the image byte array
	 * will be dumped or not. Returns a new byte array with the remaining bytes of the JPEG image. The scale factor of
	 * the remaining bytes in the image array can be be configured in the "device.properties" property file in the
	 * resource folder.
	 *
	 * @param   media	image byte array
	 * @return  new image byte array optimized for the appropriate device type
	 */
	fun refactorByteArray(media: ByteArray, deviceType: Devices): ByteArray {
		val context = AnnotationConfigApplicationContext()
		context.scan("com.restapi.application.database")
		context.refresh()

		val size = try {
			when (deviceType) {
				Devices.Mobile -> context.getBean(ProgressiveConfigurationBeans::class.java).getMobileScale()
				Devices.Tablet -> context.getBean(ProgressiveConfigurationBeans::class.java).getTabletScale()
				Devices.Desktop -> context.getBean(ProgressiveConfigurationBeans::class.java).getDesktopScale()
			}
		} catch (e: IOException) {
			logger.error("IOException occurred while reading 'device.properties' file! Return value of property " +
					"'desktop.image.load.scale' ...\n\t${e.message}")
			context.getBean(ProgressiveConfigurationBeans::class.java).getDesktopScale()
		} catch (e: java.lang.IllegalArgumentException) {
			logger.error("IllegalArgumentException occurred while reading 'device.properties' file! Return value of " +
					"property 'desktop.image.load.scale' ...\n\t${e.message}")
			context.getBean(ProgressiveConfigurationBeans::class.java).getDesktopScale()
		}
		
		logger.info("Device type: $deviceType || Image load scale: $size")
		val newByteArraySize = (media.size.toDouble() * size).toInt()
		return media.copyOfRange(0, newByteArraySize)
	}
}