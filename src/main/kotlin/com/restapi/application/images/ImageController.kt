package com.restapi.application.images

import com.restapi.application.database.ProgressiveRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

/**
 * This controller handles incoming http requests for returning an html page that displayes all available progressive
 * images.
 *
 * @author      Markus Graf, Aysenur Akyldiz
 * @see         com.restapi.application.database.ProgressiveRepository
 * @see         org.slf4j.LoggerFactory
 * @see         org.springframework.beans.factory.annotation.Autowired
 * @see         org.springframework.stereotype.Controller
 * @see         org.springframework.web.bind.annotation.GetMapping
 * @see         org.springframework.web.servlet.ModelAndView
 */
@Controller
class ImageController {
    private val logger = LoggerFactory.getLogger(ImageController::class.java)

    @Autowired
    private var progressiveRepository: ProgressiveRepository? = null
    
    /**
     * Returns a html file as response to the browser when requesting the url "localhost:8080/showAllImages" and
     * "127.0.0.1:8080/showAllImages". The html file contains links to all progressive images saved in the database.
     *
     * @param   modelAndView    holder for both Model and View in the web MVC framework
     * @return  html file name
     * @see		GetMapping      value = ["/showAllImages"]
     * @see     org.springframework.web.servlet
     */
    @GetMapping(value = ["/showAllImages"])
    fun showImages(modelAndView: ModelAndView): String {
        val images = progressiveRepository!!.findAll()
    
        logger.info("Url '/showAllImages' requested")
    
        try {
            modelAndView.addObject("images", images)
        } catch (e: Exception) {
            logger.error("Error while manipulating model and view object!\n\t${e.message}")
        }
    
        return "images"
    }
}