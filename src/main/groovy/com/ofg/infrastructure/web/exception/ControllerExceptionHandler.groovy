package com.ofg.infrastructure.web.exception

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

/**
 * Advice on {@link BadParametersException} and {@link Exception} that 
 * catches uncaught exceptions, logs and present them
 * 
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see BadParametersException
 * @see BadParameterError
 */
@Slf4j
@ControllerAdvice
@TypeChecked
class ControllerExceptionHandler {

    private static final String INTERNAL_ERROR = "internal error"

    @ExceptionHandler(BadParametersException.class)
    @ResponseBody
    public List<BadParameterError> handleBadParametersExceptions(BadParametersException exception, HttpServletResponse response) {
        response.status = SC_BAD_REQUEST
        return getListOfBindErrors(exception)
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, String> handleAnyOtherExceptions(Exception exception, HttpServletResponse response) {
        response.status = SC_BAD_REQUEST
        log.error("Unexpected exception: ", exception)
        return [error: INTERNAL_ERROR, message: exception.message]
    }

    private List<BadParameterError> getListOfBindErrors(BadParametersException exception) {
        List<BadParameterError> bindErrorList = []
        exception.errors.each {
            bindErrorList.add(getBindError(it))
        }
        return bindErrorList
    }

    private BadParameterError getBindError(ObjectError error) {
        new BadParameterError(error.properties['field'].toString(), error.defaultMessage)
    }
}
