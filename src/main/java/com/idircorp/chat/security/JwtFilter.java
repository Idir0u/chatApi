package com.idircorp.chat.security;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.idircorp.chat.entity.Client;
import com.idircorp.chat.service.ClientService;
import com.idircorp.chat.service.JwtService;

import java.util.Arrays;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService tokenService;

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            logger.info("Entering the condition where cookies are not null.");
            token = Arrays.stream(cookies)
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
            logger.info("Token after retrieving it from the cookie in webServlet: " + token);
        }
        String username = null;

        if (token != null && tokenService.validateToken(token)) {
            username = tokenService.getUsernameFromToken(token).toString();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<Client> client = clientService.getClientByUsername(username);

                if (client.isPresent()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
