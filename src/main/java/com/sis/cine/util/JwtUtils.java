package com.sis.cine.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.user.generetor}")
    private String userGeneretor;

    public String createToken(Authentication authentication) {
        Algorithm  algorithm = Algorithm.HMAC256(secret);
        String Username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(this.userGeneretor)
                .withSubject(Username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);



        return jwtToken;
    }
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGeneretor)
                    .build();
            DecodedJWT decoded = verifier.verify(token);
            return decoded;

        }catch (JWTVerificationException e) {
            throw new JWTVerificationException("invalid token" +
                    "error: " + e.getMessage());
        }
    }
    public String getUsernameFromToken(DecodedJWT decoded) {
        return decoded.getSubject().toString();

    }
    public Claim getClaimFromToken(DecodedJWT decoded, String claimName) {
        return decoded.getClaim(claimName);
    }
    public Map<String, Claim> getAllClaimsFromToken(DecodedJWT decoded) {
        return decoded.getClaims();
    }



}
