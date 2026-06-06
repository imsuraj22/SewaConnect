package com.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Validates {@code X-Internal-Api-Key} for {@code POST /api/ngos/register/{userId}} so only user-service (or peers with the key) can create NGO stubs.
 */
@Component
public class NgoRegisterInternalKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.internal-api-key}")
    private String expectedKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String context = request.getContextPath() != null ? request.getContextPath() : "";
        if (!path.startsWith(context)) {
            filterChain.doFilter(request, response);
            return;
        }
        String servletPath = path.substring(context.length());
        if (!servletPath.startsWith("/api/ngos/register/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String provided = request.getHeader("X-Internal-Api-Key");
        if (!constantTimeEquals(expectedKey, provided)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user-service",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }
}
