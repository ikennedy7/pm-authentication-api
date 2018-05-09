/*
 * $Id: k523163 JWTAuthenticationFilter, v1.0 2/14/2017 4:32 PM $
 *
 * Copyright (c) 2017 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security.jwt;

import com.heb.authentication.security.jwttoken.JwtAuthenticationToken;
import com.heb.authentication.security.model.token.RawAccessJwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//@Component
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private final AuthenticationFailureHandler failureHandler;
    private String headerString = "Authorization";
    private String tokenPrefix = "Bearer: ";
    @Autowired
	private JWTAuthenticationService jwtAuthenticationService ;


    @Autowired
    public JWTAuthenticationFilter(AuthenticationFailureHandler failureHandler,
                                                   RequestMatcher matcher) {
        super(matcher);
        this.failureHandler = failureHandler;

    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Cookie[] cookies = request.getCookies();
        String token = "";
        logger.trace("Inside cookie");
        if (cookies != null) {
            Map cookieMap = new HashMap();

            if (cookies.length != 0) {
                for (Cookie cookie : cookies) {
                    logger.trace("Inside cookie" + cookie.getName() + ":val: " + cookie.getValue());
                    cookieMap.put(cookie.getName(), cookie.getValue());
                    if (headerString.equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                    logger.trace("token" + token);
                }
                logger.trace("Inside cookie" + cookieMap.toString());
            } else {
                token = null;
            }
        }


        RawAccessJwtToken jwttoken = new RawAccessJwtToken(token.replace(tokenPrefix, ""));
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(jwttoken));
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

}
