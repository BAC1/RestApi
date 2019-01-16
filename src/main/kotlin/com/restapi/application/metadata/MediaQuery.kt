package com.restapi.application.metadata

import com.restapi.application.devices.Devices
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.IOException
import java.lang.IllegalArgumentException

/**
 * This class returns the device type depending on the width of the requesting device.
 *
 * @author      Markus Graf
 * @see 		com.restapi.application.devices.Devices
 * @see 		org.slf4j.LoggerFactory
 * @see 		org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see 		java.io.IOException
 * @see 		java.lang.IllegalArgumentException
 */
class MediaQuery {
	private val logger = LoggerFactory.getLogger(MediaQuery::class.java)

	/**
	 * Depending on the pre-configured device sizes in the file "device.properties" in the resource folder, the
	 * device type will be defined with the given <code>width</code> of the device display (in pixel).
	 *
	 * @param   width	device's display width (in pixel)
	 * @return  device type (Mobile, Tablet, Desktop)
	 */
	fun get(width: String): Devices {
		val context = AnnotationConfigApplicationContext()
		context.scan("com.restapi.application.metadata")
		context.refresh()

		try {
			val deviceType = if (width.toInt() <= context.getBean(MediaQueryBeans::class.java).getMobileWidth()) {
				Devices.Mobile
			} else if (width.toInt() <= context.getBean(MediaQueryBeans::class.java).getTabletWidth()) {
				Devices.Tablet
			} else {
				Devices.Desktop
			}
			
			logger.info("Device type '$deviceType' with width $width px detected")
			return deviceType
		} catch (e: IOException) {
			logger.error("IOException occurred while reading 'device.properties' file! Return value " +
					"'Devices.Desktop' ...\n\t${e.message}")
			return Devices.Desktop
		} catch (e: IllegalArgumentException) {
			logger.error("IllegalArgumentException occurred while reading 'device.properties' file! Return value " +
					"'Devices.Desktop' ...\n\t${e.message}")
			return Devices.Desktop
		}
	}
}