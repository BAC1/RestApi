package com.restapi.application.welcome

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WelcomeController {

    @GetMapping("/")
    fun showWelcomePage(): String {
        return "index"
    }
}