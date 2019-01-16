package com.restapi.application.database

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

/**
 * This class loads the scale factor properties
 *
 * @author      Markus Graf
 * @see 		org.slf4j.LoggerFactory
 * @see 		org.springframework.beans.factory.annotation.Autowired
 * @see 		org.springframework.context.annotation.Bean
 * @see 		org.springframework.context.annotation.Configuration
 * @see 		org.springframework.context.annotation.PropertySource
 * @see 		org.springframework.core.env.Environment
 */
@Configuration
@PropertySource(value = ["classpath:device.properties"])
class ProgressiveConfigurationBeans {
	private val logger = LoggerFactory.getLogger(ProgressiveConfigurationBeans::class.java)

	@Autowired
	lateinit var env: Environment

	/**
	 * This @Bean loads the scale factor for mobile devices
	 *
	 * @return  scale factor for mobile devices
	 */
	@Bean
	fun getMobileScale(): Double = env.getProperty("mobile.image.load.scale")!!.toDouble()

	/**
	 * This @Bean loads the scale factor for tablet devices
	 *
	 * @return  scale factor for tablet devices
	 */
	@Bean
	fun getTabletScale(): Double = env.getProperty("tablet.image.load.scale")!!.toDouble()

	/**
	 * This @Bean loads the scale factor for desktop devices
	 *
	 * @return  scale factor for desktop devices
	 */
	@Bean
	fun getDesktopScale(): Double = env.getProperty("desktop.image.load.scale")!!.toDouble()
}