package com.restapi.application.error

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.RequestDispatcher.ERROR_STATUS_CODE

@Controller
class MyErrorController: ErrorController {

    fun MyErrorController() {}

    @GetMapping(value = ["/error"])
    fun handleError(request: HttpServletRequest): String {

        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        if (status != null) {
            val statusCode = Integer.valueOf(status.toString())

            return when (statusCode) {
                HttpStatus.NOT_FOUND.value() -> "error-404"
                HttpStatus.INTERNAL_SERVER_ERROR.value() -> "error-500"
                else -> "error"
            }
        }

        return "error"
    }

    override fun getErrorPath(): String = "/error"
}