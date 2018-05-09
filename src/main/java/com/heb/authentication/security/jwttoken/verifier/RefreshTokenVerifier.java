package com.heb.authentication.security.jwttoken.verifier;

import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.service.UserJTIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenVerifier
 * 

 */
@Component
public class RefreshTokenVerifier implements TokenVerifier {
    @Autowired
    private UserJTIService userJTIService;

    @Override
    public boolean verify(String jti) {
        UserJTIEntity userJTIEntity = userJTIService.findbyJTI(jti);
        if (userJTIEntity!=null) return true ; else return false;
    }

}
