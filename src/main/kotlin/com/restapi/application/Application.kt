package com.restapi.application

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main class of the Spring MVC project.
 *
 * @author      Markus Graf
 * @see         org.springframework.boot.SpringApplication
 * @see         org.springframework.boot.autoconfigure.SpringBootApplication
 */

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}