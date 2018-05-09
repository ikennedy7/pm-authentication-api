package com.heb.authentication.controller;

import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.exception.InvalidJwtToken;
import com.heb.authentication.security.HebLdapUserService;
import com.heb.authentication.security.jwttoken.JwtSettings;
import com.heb.authentication.security.jwttoken.verifier.TokenVerifier;
import com.heb.authentication.security.model.token.JwtTokenFactory;
import com.heb.authentication.security.model.token.RawAccessJwtToken;
import com.heb.authentication.security.model.token.RefreshToken;
import com.heb.authentication.service.UserJTIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RefreshTokenEndpoint

 */
@RestController
public class RefreshTokenEndpoint {
    private static Logger logger = LoggerFactory.getLogger(RefreshTokenEndpoint.class);
    private final JwtSettings jwtSettings;

    @Autowired
    public RefreshTokenEndpoint(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }
    @Autowired
    private JwtTokenFactory tokenFactory;

    @Autowired
    private TokenVerifier tokenVerifier;


    @Autowired
    private HebLdapUserService userService;

    @Autowired
    UserJTIService userJTIService;


    @RequestMapping(value="/token", method= RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })

    public
    List<String> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.info("inside refresh token ");

        //String tokenPayload = request.getHeader(AppConstants.REFRESHHEADER);
        Cookie[] cookies  = request.getCookies();
        Map cookieMap = new HashMap();
        logger.trace("Inside /refresh token");
        Cookie refresh = null;
        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie.getValue());
            if("refresh_token".equals(cookie.getName())){
                refresh=cookie;
            }
            logger.info("cookie name: values "+cookie.getName()+":"+ cookie.getValue());
        }
        String refreshCookie = jwtSettings.getRefreshHeader();
        String tokenPayload  = cookieMap.get(jwtSettings.getRefreshHeader()).toString();

        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());

        String jti = refreshToken.getJti();


        UserJTIEntity userJTIEntity = userJTIService.findbyJTI(jti);

        String subject = refreshToken.getSubject();

        // chk users presence in LDAP
        // get authorities from ARBAF and load UserDetails


        UserDetails userDetails = userService.loadUserByUsername(subject);



        //set new accesstoken & extended refresh token as header;

        String accesstoken = (tokenFactory.createAccessJwtToken(userDetails)).toString();

        String refreshtoken = tokenFactory.createRefreshToken(userJTIEntity);

        response.addHeader("Authorization1",  "Bearer:" + " " + accesstoken );
        response.addHeader(jwtSettings.getRefreshHeader() , refreshtoken);

        logger.info("new accesstoken :"+accesstoken);
        logger.info("new refreshtoken :"+refreshtoken);

        List<String> cookiesList = new ArrayList<String>();
        cookiesList.add(accesstoken);
        cookiesList.add(refreshtoken);

        return cookiesList;
    }
}
