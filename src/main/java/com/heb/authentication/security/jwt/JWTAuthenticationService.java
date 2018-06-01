/*
 * $Id: k523163 JWTAuthenticationService, v1.0 2/14/2017 4:01 PM $
 *
 * Copyright (c) 2017 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security.jwt;


import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.exception.InvalidJwtToken;
import com.heb.authentication.security.HebUserDetails;
import com.heb.authentication.security.jwttoken.JwtSettings;
import com.heb.authentication.security.jwttoken.verifier.TokenVerifier;
import com.heb.authentication.security.model.Scopes;
import com.heb.authentication.security.model.token.RawAccessJwtToken;
import com.heb.authentication.security.model.token.RefreshToken;
import com.heb.authentication.service.UserJTIService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Component

public class JWTAuthenticationService {

    private static Logger logger = LoggerFactory.getLogger(JWTAuthenticationService.class);

    @Autowired
    UserJTIService userJTIService;

    @Autowired
    JwtSettings jwtSettings;

    @Autowired
    private TokenVerifier tokenVerifier;


        private Key signingKey;
        private String tokenPrefix = "Bearer:";
        private String headerString = "Authorization";


    /**
     * Build the JWT to be assigned to the client upon initial request
     *
     * @param response the response
     * @param user the user
     */
    void addAuthentication(HttpServletResponse response, User user) throws UnsupportedEncodingException {

        // Creating Access token JWT
        SignatureAlgorithm sigAlgorithm = SignatureAlgorithm.HS512;
        Claims claims=Jwts.claims().setSubject(user.getUsername());

        claims.put("scopes",user.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
        List rolelist = user.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList());
        claims.put("id", user.getUsername());
        claims.put("username", ((HebUserDetails) user).getDisplayName());

        LocalDateTime currentTime = LocalDateTime.now();
        String secretKeyWord = jwtSettings.getTokenSigningKey();
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKeyWord);
        signingKey = new SecretKeySpec(secretKeyBytes, sigAlgorithm.getJcaName());
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtSettings.getTokenIssuer())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(jwtSettings.getTokenExpirationTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        // Creating Refresh token
        Claims claimsrefresh = Jwts.claims().setSubject(user.getUsername());
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

        String refreshtoken = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtSettings.getTokenIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(jwtSettings.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKey())
                .compact();
        // creating refresh token

        RawAccessJwtToken rawToken = new RawAccessJwtToken(refreshtoken);
        UserJTIEntity userJTIEntity =null;

        try {
            userJTIEntity = userJTIService.findbyusername(user.getUsername());
        }catch(IncorrectResultSizeDataAccessException e){
            //TODO delete the same ID records: many records exist for the same user-id

        }
        if (userJTIEntity!=null){

            userJTIService.deleteUserEntity(userJTIEntity);
        }
        RefreshToken refreshTokenToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
        Date logintime = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        String jti = refreshTokenToken.getJti();
        UserJTIEntity userJTI = new UserJTIEntity(user.getUsername(),jti,logintime,false);
        userJTIService.saveUserJTI(userJTI);
        response.addHeader(headerString,  tokenPrefix + " " + jwt + " " + jwtSettings.getRefreshHeader() + " " + refreshtoken );
        jwt = tokenPrefix + jwt;
        // add access cookie
        Cookie accesscookie = new Cookie(headerString, jwt);
        accesscookie.setPath("/");
        Cookie refreshcookie = new Cookie(jwtSettings.getRefreshHeader(),refreshtoken);
        //add refresh cookie
        refreshcookie.setPath("/");
        response.addCookie(accesscookie);
        response.addCookie(refreshcookie);



    }

    /**
     * Get the token received from the client.
     * Verify the token via the signing key.
     *
     * @param request The request
     * @return Authenticated User
     */
    Authentication getAuthentication(HttpServletRequest request) {
        //String token = request.getHeader(headerString);
        Cookie[] cookies  = request.getCookies();
        String token = "";
        logger.trace("Inside cookie" );
        if (cookies!=null){
            Map cookieMap = new HashMap();

            if (cookies.length != 0){
            for(Cookie cookie : cookies){
                logger.trace("Inside cookie" + cookie.getName()+":val: "+ cookie.getValue());
                cookieMap.put(cookie.getName(), cookie.getValue());
                if(headerString.equals(cookie.getName())){
                    token = cookie.getValue();
                }
                logger.trace("token" + token);
            }
            logger.trace("Inside cookie" + cookieMap.toString());
            //token  = cookieMap.get("headerString").toString();
            }else { token =null;}
            logger.trace("Inside cookie");
            if (! token.isEmpty()) {
                // parse the token using the Jwts lib
                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(jwtSettings.getTokenSigningKey())
                        .parseClaimsJws(token.replace(tokenPrefix, ""));
                List<String> scopes = claims.getBody().get("scopes", List.class);
                String username = claims.getBody().getSubject();
                return username != null ?
                        new UsernamePasswordAuthenticationToken(username, null, emptyList()) :
                        null;
            }
        }
        return null;
    }

   public void removeAuthentication(HttpServletRequest request,
                              HttpServletResponse response, Authentication authentication){
        logger.trace("Inside removeAuthentication" );
        Cookie[] cookies  = request.getCookies();
        Map cookieMap = new HashMap();

        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        String tokenPayload  = cookieMap.get(jwtSettings.getRefreshHeader()).toString();

        //RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        //RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
       String jti;
       try {

           Jws<Claims> claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(tokenPayload);
           jti = claims.getBody().getId();
       } catch (ExpiredJwtException e){
           jti = e.getClaims().getId();// if claims has expired
       }
        // No need to Validate if present in JTI DB for Logoff too


        //find the JTI for the refresh token and delete from DB table USERJTI
        UserJTIEntity userJTIEntity = userJTIService.findbyJTI(jti);

        if (userJTIEntity!=null){

           userJTIService.deleteUserEntity(userJTIEntity);
        }
        logger.trace("deleted from user refresh JTI from DB " );
        Cookie refreshcookie = new Cookie(jwtSettings.getRefreshHeader().toString(), "");
        refreshcookie.setMaxAge(0);
        refreshcookie.setValue(null);
        refreshcookie.setPath("/");
        Cookie accesscookie = new Cookie("Authorization", "");
        accesscookie.setMaxAge(0);
        accesscookie.setValue(null);
        accesscookie.setPath("/");
        response.addCookie(accesscookie);
        response.addCookie(refreshcookie);
        logger.trace("deleted cookies... " );
    }
}
