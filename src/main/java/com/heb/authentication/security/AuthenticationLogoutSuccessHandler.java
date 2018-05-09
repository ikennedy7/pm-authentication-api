/**
 * 
 */
package com.heb.authentication.security;


import com.heb.authentication.security.jwt.JWTAuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author u656961
 *
 */


@Component
public class AuthenticationLogoutSuccessHandler extends
		SimpleUrlLogoutSuccessHandler {

	private static final Log logger = LogFactory.getLog(SpringCacheBasedUserCache.class);

	@Autowired
	private JWTAuthenticationService jwtAuthenticationService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		logger.info("inside logout success handler $$");
		jwtAuthenticationService.removeAuthentication(request, response, authentication);

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
