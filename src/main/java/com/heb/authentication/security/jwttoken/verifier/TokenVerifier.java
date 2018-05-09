package com.heb.authentication.security.jwttoken.verifier;

/**
 * 

 */
public interface TokenVerifier {
    public boolean verify(String jti);
}
