package com.restapi.application.metadata

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

/**
 * This class loads the max width properties for mobile and tablet devices
 *
 * @author      Markus Graf
 * @see 		org.springframework.beans.factory.annotation.Autowired
 * @see 		org.springframework.context.annotation.Bean
 * @see 		org.springframework.context.annotation.Configuration
 * @see 		org.springframework.context.annotation.PropertySource
 * @see 		org.springframework.core.env.Environment
 */
@Configuration
@PropertySource(value = ["classpath:device.properties"])
class MediaQueryBeans {

	@Autowired
	lateinit var env: Environment

	/**
	 * This @Bean loads the max width for mobile devices
	 *
	 * @return  max width for device type 'mobile' (in pixel)
	 */
	@Bean
	fun getMobileWidth(): Int = env.getProperty("mobile.display.width")!!.toInt()

	/**
	 * This @Bean loads the max width for tablet devices
	 *
	 * @return  max width for device type 'tablet' (in pixel)
	 */
	@Bean
	fun getTabletWidth(): Int = env.getProperty("tablet.display.width")!!.toInt()
}