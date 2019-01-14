package com.restapi.application.images

import com.restapi.application.database.Progressive
import com.restapi.application.database.ProgressiveController
import com.restapi.application.database.ProgressiveRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(path=["/images"])
class ImageController {
    private val logger = LoggerFactory.getLogger(ProgressiveController::class.java)

    @Autowired
    private var progressiveRepository: ProgressiveRepository? = null

    @GetMapping(value = ["/showAll"])
    fun showImages(modelAndView: ModelAndView, model: Model): String  {
        val images = progressiveRepository!!.findAll()

        for ((index, image) in images.withIndex()) {
            image as Progressive

            modelAndView.addObject("var${index + 1}", image.getName())
        }

        if (images.count() < 5) {
            var count = images.count() + 1

            while (count < 6) {
                modelAndView.addObject("var$count", "")
                count++
            }
        }

        return "images"
    }
}