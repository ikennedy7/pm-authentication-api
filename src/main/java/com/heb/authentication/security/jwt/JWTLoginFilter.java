/*
 * $Id: k523163 JWTLoginFilter, v1.0 2/14/2017 3:05 PM $
 *
 * Copyright (c) 2017 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heb.authentication.security.HebUserDetails;
import com.heb.authentication.service.UserJTIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger logger = LoggerFactory.getLogger(JWTLoginFilter.class);
    private static final String USERNAME_ID_KEY = "id";
    private String url;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private ObjectMapper objectMapper;

    @Autowired
	private JWTAuthenticationService jwtAuthenticationService ;
    @Autowired
	private UserJTIService userJTIService;

    public JWTLoginFilter(String url, AuthenticationSuccessHandler successHandler,
						  AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(new AntPathRequestMatcher(url));
        logger.trace("Inside JWTLoginFilter");
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;


    }

    /**
     * Performs actual authentication.
     * <p>
     * The implementation should do one of the following:
     * <ol>
     * <li>Return a populated authentication token for the authenticated user, indicating successful authentication</li>
     * <li>Return null, indicating that the authentication process is still in progress. Before returning, the
     * implementation should perform any additional work required to complete the process.</li>
     * <li>Throw an <tt>AuthenticationException</tt> if the authentication process fails</li>
     * </ol>
     *
     * @param request  from which to extract parameters and perform the authentication
     * @param response the response, which may be needed if the implementation has to do a redirect as part of a
     *                 multi-stage authentication process (such as OpenID).
     * @return the authenticated user token, or null if authentication is incomplete.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
			AuthenticationException, IOException, ServletException {
        UserCredentials userDetails = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword()
                );
        return getAuthenticationManager().authenticate(token);
    }

    /**
     * Default behaviour for successful authentication.
     * <ol>
     * <li>Sets the successful <tt>Authentication</tt> object on the {@link SecurityContextHolder}</li>
     * <li>Informs the configured <tt>RememberMeServices</tt> of the successful login</li>
     * <li>Fires an {@link InteractiveAuthenticationSuccessEvent} via the configured
     * <tt>ApplicationEventPublisher</tt></li>
     * <li>Delegates additional behaviour to the {@link AuthenticationSuccessHandler}.</li>
     * </ol>
     * <p>
     * Subclasses can override this method to continue the {@link FilterChain} after successful authentication.
     *
     * @param request
     * @param response
     * @param chain
     * @param authentication the object returned from the <tt>attemptAuthentication</tt> method.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        logger.info("user" + user.toString());
        successHandler.onAuthenticationSuccess(request, response, authentication);


    }

    /**
     * Map the user details to a node
     *
     * @param user
     * @param authorities
     * @return ObjectNode
     * @throws JsonProcessingException
     */
    private ObjectNode mapUserDetails(User user, List<GrantedAuthority> authorities)
            throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        ArrayNode roles = mapper.createArrayNode();
        node.put(USERNAME_ID_KEY, user.getUsername());
        if (user instanceof HebUserDetails) {
            node.put("name", ((HebUserDetails) user).getDisplayName());
            node.put("corpId", ((HebUserDetails) user).getHebGLlocation());
            for (GrantedAuthority authority : authorities) {
                String auth = authority.getAuthority();
                roles.add(auth);
            }
            node.put("roles", mapper.writeValueAsString(roles));
        }
        return node;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
