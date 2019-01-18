package com.restapi.application.error

import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

/**
 * This controller handles incoming http requests for error messages.
 *
 * @author      Markus Graf
 * @see         org.slf4j.LoggerFactory
 * @see         org.springframework.boot.web.servlet.error.ErrorController
 * @see         org.springframework.http.HttpStatus
 * @see         org.springframework.stereotype.Controller
 * @see         org.springframework.web.bind.annotation.GetMapping
 * @see         javax.servlet.RequestDispatcher
 * @see         javax.servlet.http.HttpServletRequest
 */
@Controller
class MyErrorController : ErrorController {
    private val logger = LoggerFactory.getLogger(MyErrorController::class.java)
    
    fun MyErrorController() {}
    
    /**
     * Returns the html file as response to the browser when requesting an unknown url of the domain "localhost:8080"
     * and "127.0.0.1:8080" or when an error occurred by requesting a legit url (e.g. Internal Server Error). The
     * html file contains an error message for the appropriate error code.
     *
     * @param   request         http request sent by browser
     * @return  html file name for appropriate error code
     * @see		GetMapping      value = ["/error"]
     * @see     org.springframework.boot.web.servlet.error.ErrorController
     */
    @GetMapping(value = ["/error"])
    fun handleError(request: HttpServletRequest): String {
        val errorCode = try {
            getAttributeOfErrorCode(
                    request = request,
                    errorCode = RequestDispatcher.ERROR_STATUS_CODE
            ).toString()
        } catch (e: Exception) {
            logger.error("Error while manipulating model and view object!\n\t${e.message}")
            HttpStatus.INTERNAL_SERVER_ERROR.value().toString()
        }
    
        logger.warn("Error page for code '$errorCode' requested!")
        return returnErrorPage(errorCode)
    }
    
    /**
     * Returns the path of the general error page.
     *
     * @return  the error path
     */
    override fun getErrorPath(): String = "/error"
    
    /**
     * Retrieves the error code of the incoming http request.
     *
     * @param   request     http request for which an error occurred
     * @param   errorCode   actual error code of the given request
     * @return  <code>error status code</code> of the given request, or <code>null</code> if the attribute isn't available
     */
    private fun getAttributeOfErrorCode(request: HttpServletRequest, errorCode: String): Any? = request.getAttribute(errorCode)
    
    /**
     * Returns the html file name of the appropriate error code.
     *
     * @param   errorCode    http error code for which a error page shall be displayed
     * @return  html file name of the appropriate error code, or <code>error</code> if the given error code isn't supported
     */
    private fun returnErrorPage(errorCode: String): String {
        return when (errorCode) {
            HttpStatus.NOT_FOUND.value().toString() -> "error-404"
            HttpStatus.INTERNAL_SERVER_ERROR.value().toString() -> "error-500"
            else -> "error"
        }
    }
}