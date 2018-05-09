package com.heb.authentication.security.jwttoken.extractor;

/**
 * Implementations of this interface should always return raw base-64 encoded
 * representation of JWT Token.
 * 

 */
public interface TokenExtractor {
    public String extract(String payload);
}
