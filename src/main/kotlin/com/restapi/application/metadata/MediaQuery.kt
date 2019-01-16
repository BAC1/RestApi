package com.restapi.application.metadata

import com.restapi.application.devices.Devices
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import java.io.IOException
import java.lang.IllegalArgumentException

/**
 * This class returns the device type depending on the width of the requesting device.
 *
 * @author      Markus Graf
 * @see 		com.restapi.application.devices.Devices
 * @see 		org.slf4j.LoggerFactory
 * @see 		org.springframework.beans.factory.annotation.Value
 * @see 		org.springframework.context.annotation.Configuration
 * @see 		org.springframework.context.annotation.PropertySource
 * @see 		java.io.IOException
 * @see 		java.lang.IllegalArgumentException
 */
@Configuration
@PropertySource("classpath:device.properties")
class MediaQuery {
	private val logger = LoggerFactory.getLogger(MediaQuery::class.java)
	
	@Value("\${mobile.display.width}")
	private val mobileDisplayWidth: Int = 0
	
	@Value("\${tablet.display.width}")
	private val tabletDisplayWidth: Int = 0
	
	/**
	 * Depending on the pre-configured device sizes in the file "device.properties" in the resource folder, the
	 * device type will be defined with the given <code>width</code> of the device display (in pixel).
	 *
	 * @param   width	device's display width (in pixel)
	 * @return  device type (Mobile, Tablet, Desktop)
	 */
	fun get(width: String): Devices {
		try {
			val deviceType = if (width.toInt() <= mobileDisplayWidth) {
				Devices.Mobile
			} else if (width.toInt() <= tabletDisplayWidth) {
				Devices.Tablet
			} else {
				Devices.Desktop
			}
			
			logger.info("Device type '$deviceType' with width $width px detected")
			return deviceType
		} catch (e: IOException) {
			logger.error("IOException occurred while reading 'device.properties' file! Return value 'Devices.Desktop'")
			return Devices.Desktop
		} catch (e: IllegalArgumentException) {
			logger.error("IllegalArgumentException occurred while reading 'device.properties' file! Return value " +
					"'Devices.Desktop'")
			return Devices.Desktop
		}
	}
}