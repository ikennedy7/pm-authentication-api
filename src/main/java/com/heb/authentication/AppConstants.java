/**
 *
 */
package com.heb.authentication;

/**
 * The type App constants.
 *
 * @author u656961
 */
public class AppConstants {

    private AppConstants() {

    }

    //Profiles
    /**
     * The constant SPRING_PROFILE_LOCAL.
     */
    public static final String SPRING_PROFILE_LOCAL = "local";
    /**
     * The constant SPRING_PROFILE_DEVELOPMENT.
     */
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    /**
     * The constant SPRING_PROFILE_CERTIFICATION.
     */
    public static final String SPRING_PROFILE_CERTIFICATION = "cert";
    /**
     * The constant SPRING_PROFILE_PRODUCTION.
     */
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    //URLs
    /**
     * The constant URL_LOGIN.
     */
    public static final String URL_LOGIN = "/login";

    //Logging
    /**
     * The constant ENTRY.
     */
    public static final String ENTRY = "ENTRY";
    /**
     * The constant EXIT.
     */
    public static final String EXIT = "EXIT";

    public static final int DEFAULT_FLAG_OR_SWITCH_LENGTH = 1;

}
