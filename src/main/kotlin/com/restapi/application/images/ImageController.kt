package com.restapi.application.images

import com.restapi.application.database.Progressive
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
 * @author      Markus Graf
 * @see         com.restapi.application.database.Progressive
 * @see         com.restapi.application.database.ProgressiveRepository
 * @see         org.slf4j.LoggerFactory
 * @see         org.springframework.beans.factory.annotation.Autowired
 * @see         org.springframework.stereotype.Controller
 * @see         org.springframework.web.bind.annotation.GetMapping
 * @see         org.springframework.web.bind.annotation.RequestMapping
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
     * @param   modelAndView   holder for both Model and View in the web MVC framework
     * @return  html file name
     * @see     org.springframework.web.servlet
     */
    @GetMapping(value = ["/showAllImages"])
    fun showImages(modelAndView: ModelAndView): String {
        logger.warn("Url '/showAllImages' requested")
        val images = progressiveRepository!!.findAll()
    
        addImagesToModel(
                modelAndView = modelAndView,
                images = images
        )
    
        setDefaultValuesForNonUsedThymeleafVariables(
                modelAndView = modelAndView,
                images = images
        )
    
        return "images"
    }
    
    /**
     * Adds the given images to the "ModelAndView" object.
     *
     * @param   images          images that shall be added to the "ModelAndView" object
     * @param   modelAndView    object to which the given images shall be added
     */
    private fun addImagesToModel(modelAndView: ModelAndView, images: MutableIterable<Progressive>) {
        for ((index, image) in images.withIndex()) {
            modelAndView.addObject("var${index + 1}", image.getName())
        }
    }
    
    /**
     * Fills unused Thymeleaf variables in the html file with empty strings. Un-instantiated variables leads to an
     * Exception in the Thymeleaf framework.
     *
     * @param   images          set of iterable images that shall be added to the "ModelAndView" object
     * @param   modelAndView    object to which the given images shall be added
     */
    private fun setDefaultValuesForNonUsedThymeleafVariables(modelAndView: ModelAndView, images: MutableIterable<Progressive>) {
        if (images.count() < 5) {
            var count = images.count() + 1
        
            while (count < 6) {
                modelAndView.addObject("var$count", "")
                count++
            }
        }
    }
}