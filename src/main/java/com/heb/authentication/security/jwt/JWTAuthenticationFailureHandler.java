package com.heb.authentication.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Respond to the client that the authentication failed.
 *
 * @author vn01571
 * @since 05/22/17.
 */
@Component
public class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private  ObjectMapper mapper;

    @Autowired
    public JWTAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //TODO need to validate with UI
        /*if (exception instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), "Invalid username or password",  HttpStatus.UNAUTHORIZED));
        } else if (exception instanceof JwtExpiredTokenException) {
            mapper.writeValue(response.getWriter(), ErrorResponse.of("Token has expired", ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        }

        mapper.writeValue(response.getWriter(), ErrorResponse.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));*/
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
