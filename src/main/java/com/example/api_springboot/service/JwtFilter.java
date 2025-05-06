package com.example.api_springboot.service;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Liste des URL à exclure du filtre
    // private static final List<String> EXCLUDED_PATHS = List.of(
    //     "/client/auth",
    //     "/vendeur",
    //     "/client"
    // );

    // @SuppressWarnings("null")
    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) {
    //     String path = request.getServletPath();
    //     return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    // }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Token valid for user: " + username);  // Log pour le débogage
            } else {
                System.out.println("Invalid or expired token.");
            }
        } else {
            System.out.println("No token found in Authorization header.");
        }
        chain.doFilter(request, response);
    }
}

