package com.restapi.application

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main class of the Spring MVC project
 *
 * @author      Markus Graf
 * @see         org.springframework.boot.SpringApplication
 * @see         org.springframework.boot.autoconfigure.SpringBootApplication
 */

/**
 * @param   SpringBootApplication  enables the following three features:
                @EnableAutoConfiguration: enable Spring Boot’s auto-configuration mechanism
                @ComponentScan: enable @Component scan on the package where the application is located
                @Configuration: allow to register extra beans in the context or import additional configuration classes
 */
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}