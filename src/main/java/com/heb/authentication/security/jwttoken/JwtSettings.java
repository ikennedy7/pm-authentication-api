package com.heb.authentication.security.jwttoken;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"local", "dev", "cert"})
@Configuration
@ConfigurationProperties
public class JwtSettings {
    /**
     *
     */

    //tokenexpirationtime=5 mins
    @Value("${tokenExpirationTime}")
    private Integer tokenExpirationTime;

    // TOKENISSUER = "http://heb.com"
    @Value("${tokenIssuer}")
    private String tokenIssuer;

    //  REFRESHTOKENEXPTIME = 15 in mins
    @Value("${refreshTokenExpTime}")
    private Integer refreshTokenExpTime;

    // TOKENSIGNINGKEY = secret
    @Value("${tokenSigningKey}")
    private String tokenSigningKey;

    //refreshheader=refresh_token
    @Value("${refreshHeader}")
    private String refreshHeader;



    public Integer getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Integer tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }
    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getTokenSigningKey() {
        return tokenSigningKey;
    }

    public void setTokenSigningKey(String tokenSigningKey) {
        this.tokenSigningKey = tokenSigningKey;
    }

    public String getRefreshHeader(){ return refreshHeader;}

    public void setRefreshHeader(String refreshHeader) {
        this.refreshHeader = refreshHeader;
    }
}
