package com.pm.usersservice.filter;

import com.pm.usersservice.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/register",
            "/api/users/validate-login",
            "/api/users/find-or-create"
    );

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (PUBLIC_ENDPOINTS.contains(path) || path.matches("^/api/users/.*/validate$")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            sendUnauthorized(response, "Missing cookies");
            return;
        }

        String token = Arrays.stream(cookies)
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token == null) {
            sendUnauthorized(response, "Missing access token");
            return;
        }

        try {
            Claims claims = jwtService.validateToken(token);
            String email = claims.getSubject();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            AuthorityUtils.NO_AUTHORITIES
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            clearAccessTokenCookie(response);
            sendUnauthorized(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private void clearAccessTokenCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie("accessToken", "");
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);
    }
}