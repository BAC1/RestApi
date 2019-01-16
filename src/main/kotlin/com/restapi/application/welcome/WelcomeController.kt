package com.restapi.application.welcome

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * This controller handles incoming http requests for the welcome page.
 *
 * @author      Markus Graf
 * @see         org.slf4j.LoggerFactory
 * @see         org.springframework.stereotype.Controller
 * @see         org.springframework.web.bind.annotation.GetMapping
 */
@Controller
class WelcomeController {
    private val logger = LoggerFactory.getLogger(WelcomeController::class.java)

    /**
     * Returns the html file as response to the browser when requesting the url "localhost:8080/" and "127.0.0.1:8080/".
     *
     * @return  html file name of the welcome page
     */
    
    @GetMapping("/")
    fun showWelcomePage(): String {
        logger.info("Welcome page requested")
        return "index"
    }
}