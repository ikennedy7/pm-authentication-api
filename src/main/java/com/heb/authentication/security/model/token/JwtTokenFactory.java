package com.heb.authentication.security.model.token;

import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.security.HebUserDetails;
import com.heb.authentication.security.jwttoken.JwtSettings;
import com.heb.authentication.security.model.Scopes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Factory class that should be always used to create {@link JwtToken}.
 * 

 */
@Component
public class JwtTokenFactory {
    private final JwtSettings settings;
    private Key signingKey;
    @Autowired
    public JwtTokenFactory(JwtSettings settings) {
        this.settings = settings;
    }


    public String createAccessJwtToken(UserDetails userDetails) {
        SignatureAlgorithm sigAlgorithm = SignatureAlgorithm.HS512;
        if (userDetails.getUsername().isEmpty())
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("scopes", userDetails.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
        claims.put("id", userDetails.getUsername());
        claims.put("username", ((HebUserDetails) userDetails).getDisplayName());
        LocalDateTime currentTime = LocalDateTime.now();
        //Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String secretKeyWord = settings.getTokenSigningKey();
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKeyWord);
        signingKey = new SecretKeySpec(secretKeyBytes, sigAlgorithm.getJcaName());

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(java.util.Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(java.util.Date.from(currentTime
                        .plusMinutes(settings.getTokenExpirationTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();


        return token;
    }

    public String createRefreshToken(UserJTIEntity userJTIEntity) {
        if (userJTIEntity.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Cannot create Refresh JWT Token without username");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(userJTIEntity.getUsername());
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));
        


        String refreshtoken = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setId(userJTIEntity.getJti())
                .setIssuedAt(java.util.Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(java.util.Date.from(currentTime
                        .plusMinutes(settings.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return refreshtoken;
    }
}
