package com.worknow.backend.security;

import com.worknow.backend.model.User;
import com.worknow.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Runs once per request.
 * Extracts JWT, validates it, and enforces authentication
 * on protected endpoints.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepo;

    // Routes that REQUIRE authentication
    private static final Set<String> PROTECTED_PATH_PREFIXES = Set.of(
            "/apply",
            "/manage",
            "/post"
    );

    public JwtAuthFilter(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        boolean isProtected = PROTECTED_PATH_PREFIXES
                .stream()
                .anyMatch(path::startsWith);

        String authHeader = request.getHeader("Authorization");

        // If protected route and no token → BLOCK
        if (isProtected && (authHeader == null || !authHeader.startsWith("Bearer "))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication required");
            return;
        }

        // If token exists, try to authenticate
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Long userId = JwtUtil.getUserId(token);

                User user = userRepo.findById(userId).orElse(null);
                if (user == null) {
                    if (isProtected) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid token");
                        return;
                    }
                    chain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, null
                        );

                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

            } catch (Exception e) {
                if (isProtected) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
