/*
 * $Id: m228250 ExceptionConstants.java, v1.0 5/26/2016 10:52 AM $
 *
 * Copyright (c) 2015 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.exception;

/**
 * ExceptionConstants.java.
 *
 * @author m228250
 */
public final class ExceptionConstants {


    /**
     * Error Codes / Messages.
     */
    /**
     * 1000 General Service Side Errors.
     */
    public static final String SYSTEM_ADMIN_MESSAGE =
            "Please Contact Your System Administrator or Submit a Help Desk Ticket.";
    /**
     *  Access Is Denied.
     */
    public static final String ERROR_1000_UNAUTHORIZED =
            "Unauthorized. Access Is Denied. Error Code: [1000] " + SYSTEM_ADMIN_MESSAGE;
    /**
     * Internal Server Error.
     */
    public static final String ERROR_1001_SERVER_ERROR = "" +
            "Internal Server Error. Error Code: [1001] " + SYSTEM_ADMIN_MESSAGE;
    /**
     * Database Error.
     */
    public static final String ERROR_1002_DATABASE_ERROR = "Database Error. Error Code: [1002] " + SYSTEM_ADMIN_MESSAGE;
    /**
     * Bad Request.
     */
    public static final String ERROR_1003_BAD_REQUEST = "Bad Request. Error Code: [1003] " + SYSTEM_ADMIN_MESSAGE;
    /**
     * There Was An Error Processing Your Request.
     */
    public static final String ERROR_1004_GENERAL_ERROR =
            "There Was An Error Processing Your Request. Error Code: [1004] " + SYSTEM_ADMIN_MESSAGE;

    /**
     * 2000 Service Layer Warning Codes.
     */
    public static final String WARNING_COMPETITOR_LOCATIONS_NOT_FOUND = "No Competitors Found For Selected Store";

    /**
     * Success Responses.
     */
    public static final String GENERIC_SUCCESS_RESPONSE = "Your Changes Have Been Successfully Updated";

    /**
     * Instantiates a new Exception constants.
     */
    private ExceptionConstants() {
    }
}
