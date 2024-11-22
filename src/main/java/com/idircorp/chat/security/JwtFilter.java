package com.idircorp.chat.security;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.idircorp.chat.entity.Admin;
import com.idircorp.chat.entity.Client;
import com.idircorp.chat.service.AdminService;
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
    private AdminService adminService;

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //String header = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();
        String token = null;
        if(cookies != null){
            System.out.println("line 47!");
            logger.info("Entering the condition where cookies are not null.");
            token = Arrays.stream(cookies)
            .filter(cookie -> "jwt".equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);
            logger.info("token after retrieving it from the cookie in webServlet: "+ token);
        }
        String username = null;
        String role = null;

        if (token != null  && tokenService.validateToken( token )) {
            username = tokenService.getUsernameFromToken(token).toString();
            role = tokenService.getRoleFromToken(token);
        
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<Admin> admin = adminService.getAdminByUsername(username);
                Optional<Client> client = clientService.getClientByUsername(username);
                
                if ( ( admin.isPresent() || client.isPresent() ) ) {
                    String prefixedRole = "ROLE_" + role.toUpperCase();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority(prefixedRole)));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    
}