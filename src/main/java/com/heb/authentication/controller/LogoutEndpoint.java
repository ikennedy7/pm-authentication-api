package com.heb.authentication.controller;


import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.exception.InvalidJwtToken;
import com.heb.authentication.security.jwttoken.JwtSettings;
import com.heb.authentication.security.jwttoken.verifier.TokenVerifier;
import com.heb.authentication.security.model.token.RawAccessJwtToken;
import com.heb.authentication.security.model.token.RefreshToken;
import com.heb.authentication.service.UserJTIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * LogoutEndpoint - dummy logout endpoint never called :)

 */
@Profile({"junit", "local"})
@RestController
public class LogoutEndpoint {
    private final JwtSettings jwtSettings;

    @Autowired
    public LogoutEndpoint(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }
    @Autowired
    private TokenVerifier tokenVerifier;

    @Autowired
    UserJTIService userJTIService;

    @RequestMapping(value="/logout", method= RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    public void logOutEnd(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        //String tokenPayload = request.getHeader(AppConstants.REFRESHHEADER);
        Cookie[] cookies  = request.getCookies();
        Map cookieMap = new HashMap();

        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        String tokenPayload  = cookieMap.get(jwtSettings.getRefreshHeader()).toString();

        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
        // TODO Validate if present in JTI DB for Logoff too ?
        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }
        //find the JTI for the refresh token and delete from DB table USERJTI
        UserJTIEntity userJTIEntity = userJTIService.findbyJTI(jti);
        userJTIService.deleteUserEntity(userJTIEntity);
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
        //TODO send redirect to error page with login link
        //response.sendRedirect();

    }

    @GetMapping(value = "index")
    public String index(){
        return "This is index API and It's without Security";
    }
    @GetMapping(value = "secureAPI")
    public String secureAPI() {
        return "Spring security 5 in-memory Basic Authentication Example";
    }
}
