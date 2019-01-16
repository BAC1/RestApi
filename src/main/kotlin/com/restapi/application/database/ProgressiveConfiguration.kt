package com.restapi.application.database

import com.restapi.application.devices.Devices
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import java.io.IOException

/**
 * This class returns a re-sized byte array of the progressive-encoded JPEG.
 *
 * @author      Markus Graf
 * @see 		com.restapi.application.devices.Devices
 * @see 		org.slf4j.LoggerFactory
 * @see 		org.springframework.beans.factory.annotation.Value
 * @see 		org.springframework.context.annotation.Configuration
 * @see 		org.springframework.context.annotation.PropertySource
 * @see 		java.io.IOException
 */
@Configuration
@PropertySource("classpath:device.properties")
class ProgressiveConfiguration {
	private val logger = LoggerFactory.getLogger(ProgressiveConfiguration::class.java)
	
	@Value("\${mobile.image.load.scale}")
	private val mobileLoadScale: Double = 0.0
	
	@Value("\${tablet.image.load.scale}")
	private val tabletLoadScale: Double = 0.0
	
	@Value("\${desktop.image.load.scale}")
	private val desktopLoadScale: Double = 0.0
	
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
		val size = try {
			when (deviceType) {
				Devices.Mobile -> mobileLoadScale
				Devices.Tablet -> tabletLoadScale
				Devices.Desktop -> desktopLoadScale
			}
		} catch (e: IOException) {
			logger.error("IOException occurred while reading 'device.properties' file! Return value of property " +
					"'desktop.image.load.scale'")
			desktopLoadScale
		} catch (e: java.lang.IllegalArgumentException) {
			logger.error("IllegalArgumentException occurred while reading 'device.properties' file! Return value of " +
					"property 'desktop.image.load.scale'")
			desktopLoadScale
		}
		
		logger.info("Device type: $deviceType || Image load scale: $size")
		val newByteArraySize = (media.size.toDouble() * size).toInt()
		return media.copyOfRange(0, newByteArraySize)
	}
}