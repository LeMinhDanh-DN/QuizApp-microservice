package com.mr_n.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) || path.startsWith("/auth/") || path.startsWith("/eureka/") || path.equals("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            Object userIdObj = claims.get("userId");
            Object rolesObj = claims.get("roles");

            String userId = userIdObj != null ? userIdObj.toString() : "";
            String roles = "";
            if (rolesObj instanceof List<?>) {
                roles = String.join(",", ((List<?>) rolesObj).stream().map(Object::toString).toList());
            } else if (rolesObj != null) {
                roles = rolesObj.toString();
            }

            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
            // Remove any untrusted header passed from outside
            requestWrapper.removeHeader("X-User-Id");
            requestWrapper.removeHeader("X-User-Name");
            requestWrapper.removeHeader("X-User-Roles");

            // Add authenticated identity headers
            requestWrapper.addHeader("X-User-Id", userId);
            requestWrapper.addHeader("X-User-Name", username);
            requestWrapper.addHeader("X-User-Roles", roles);

            filterChain.doFilter(requestWrapper, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired JWT token: " + e.getMessage());
        }
    }

    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> customHeaders = new HashMap<>();

        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void addHeader(String name, String value) {
            customHeaders.put(name.toLowerCase(), value);
        }

        public void removeHeader(String name) {
            customHeaders.remove(name.toLowerCase());
        }

        @Override
        public String getHeader(String name) {
            String lowerName = name.toLowerCase();
            if (customHeaders.containsKey(lowerName)) {
                return customHeaders.get(lowerName);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> set = new HashSet<>(customHeaders.keySet());
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                if (!customHeaders.containsKey(name.toLowerCase())) {
                    set.add(name);
                }
            }
            return Collections.enumeration(set);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String lowerName = name.toLowerCase();
            if (customHeaders.containsKey(lowerName)) {
                return Collections.enumeration(Collections.singletonList(customHeaders.get(lowerName)));
            }
            return super.getHeaders(name);
        }
    }
}
