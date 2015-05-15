package com.ofg.infrastructure.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * Advice on {@link BadParametersException} and {@link Exception} that
 * catches uncaught exceptions, logs and present them
 *
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see BadParametersException
 * @see BadParameterError
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INTERNAL_ERROR = "internal error";

    @ExceptionHandler(BadParametersException.class)
    @ResponseBody
    public List<BadParameterError> handleBadParametersExceptions(BadParametersException exception, HttpServletResponse response) throws Exception {
        response.setStatus(SC_BAD_REQUEST);
        return getListOfBindErrors(exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, String> handleAnyOtherExceptions(Exception exception, HttpServletResponse response) {
        response.setStatus(SC_BAD_REQUEST);
        log.error("Unexpected exception: ", exception);
        Map<String, String> map = new HashMap<>();
        map.put("error", INTERNAL_ERROR);
        map.put("message", exception.getMessage());
        return map;
    }

    private List<BadParameterError> getListOfBindErrors(BadParametersException exception) throws Exception {
        List<BadParameterError> bindErrorList = new ArrayList<>();
        for (ObjectError error : exception.getErrors()) {
            bindErrorList.add(getBindError(error));
        }
        return bindErrorList;
    }

    private BadParameterError getBindError(ObjectError error) throws Exception {
        if (error instanceof FieldError) {
            return new BadParameterError(((FieldError) error).getField(), error.getDefaultMessage());
        }
        return new BadParameterError("Unknown field" ,error.getDefaultMessage());
    }
}
