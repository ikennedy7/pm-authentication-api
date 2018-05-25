package com.heb.authentication.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heb.authentication.security.HebUserDetails;
import com.heb.authentication.security.model.token.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Upon successful authentication, respond back to the client with user roles in JSON.
 *
 * @author p235969
 * @since 11/21/14.
 */
@Component

public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private  ObjectMapper mapper;
    private JwtTokenFactory tokenFactory;

    private static final String USERNAME_ID_KEY = "id";
    private static final String USER_ROLES_KEY = "roles";
    private static final String CORP_ID_KEY = "corpId";
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String STORE_KEY = "store";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";

    @Autowired
	private JWTAuthenticationService jwtAuthenticationService ;

    @Autowired
    public JWTAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory ) {
        this.mapper = mapper;
        this.tokenFactory = tokenFactory;

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // we need the principal to pass information back to the client
        // i.e. username and roles
        User user = (User) authentication.getPrincipal();
        if (user != null) {
            // JSON transformer for user information
            ObjectMapper mapper = new ObjectMapper();
            // we need to transform the roles into a JSON object
            ArrayNode roles = mapper.createArrayNode();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                roles.add(authority.getAuthority());
            }
            ObjectNode node = mapper.createObjectNode();
            node.put(USERNAME_ID_KEY, user.getUsername());

            // adding h-e-b user specific details
            if (user instanceof HebUserDetails) {
                node.put("name", ((HebUserDetails) user).getDisplayName());
                node.put("corpId", ((HebUserDetails) user).getHebGLlocation());
                node.put("roles", roles);
            }
            jwtAuthenticationService.addAuthentication(response, user);
            // prepare response for JSON consumption
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }

        // forget what the client did
        clearAuthenticationAttributes(request);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     *
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }


}
