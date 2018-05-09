/*
 * $Id: m228250 GlobalExceptionHandlerController, v1.21 4/28/2015 2:14 PM $
 *
 * Copyright (c) 2012 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.exception;

//import org.apache.log4j.Logger;
import org.jboss.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * GlobalExceptionHandler.java.
 * This class handles all fatal exceptions for the application. the exceptionrror is logged under ERROR and a general
 * response is returned from the exception errorCodeEnum object. Please see ErrorCodeEnum for response messages.
 *
 * @author vn01473
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);

    /**
     * Access denied exception handler.
     *
     * @param e the exception
     * @return the pat response model
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedExceptionHandler(AccessDeniedException e) {
        LOGGER.error(e.getMessage(), e);
        return ExceptionConstants.ERROR_1000_UNAUTHORIZED;
    }

    /**
     * Exception handler.
     *
     * @param e the exception
     * @return the pat response model
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class, NullPointerException.class})
    public String exceptionNullHandler(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ExceptionConstants.ERROR_1001_SERVER_ERROR;
    }

    /**
     * Data access / sql exception handler.
     *
     * @param e the exception
     * @return the pat response model
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public String dataAccessSqlExceptionHandler(DataAccessException e) {
        LOGGER.error(e.getMessage(), e);
        return ExceptionConstants.ERROR_1002_DATABASE_ERROR;
    }



    /**
     * Invalid JWT Token.
     *
     * @param e InvalidJwtToken
     * @return message
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = InvalidJwtToken.class)
    public String invalidJwtToken(InvalidJwtToken e) {
        LOGGER.warn(e.getMessage(),e);
        return "Invalid Jwt Token";
    }

    /**
     * Jwt Expired Token Exception.
     *
     * @param e JwtExpiredTokenException
     * @return message
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = JwtExpiredTokenException.class)
    public String jwtExpiredTokenException(JwtExpiredTokenException e) {
        LOGGER.warn(e.getMessage(),e);
        return "Jwt Expired";
    }

}
