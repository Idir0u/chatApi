package com.idircorp.chat.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import com.idircorp.chat.config.JwtConfig;
import com.idircorp.chat.entity.User;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService { 

    @Autowired
    private JwtConfig jwtConfig;

    private String SECRET_KEY;

    @PostConstruct
    public void init() {
        this.SECRET_KEY = jwtConfig.getSecretKey();
    }

    private Key getSigningKey() {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalStateException("Secret key is not set or is empty.");
        }
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("The secret key must be at least 32 bytes long.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());

        Key key = getSigningKey();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    

    public Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("id", Long.class);
    }

    public String getUsernameFromToken(String token) {
        String username = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
        return username;
    }


    public boolean validateToken(String token){
            try {
                Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token); // Parse to validate structure and signature
                return true;
            } catch (ExpiredJwtException e) {
                // Log specific error or handle the expired token case
                System.out.println("Token has expired: " + e.getMessage());
            } catch (MalformedJwtException e) {
                // Handle the case when the token is not correctly structured
                System.out.println("Malformed token: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                // Handle tokens with unsupported claims or formats
                System.out.println("Unsupported token: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                // Handle empty or null tokens
                System.out.println("Token argument is invalid: " + e.getMessage());
            }
            return false;
        
        
    }

    public String getRoleFromToken(String token) {
        String role = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("role", String.class); 
        return role; 
    }
}
