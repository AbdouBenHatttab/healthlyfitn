// package com.healthapp.doctor.security;

// import com.healthapp.shared.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// import java.util.List;
// import java.util.stream.Collectors;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class JwtTokenValidator extends OncePerRequestFilter {
    
//     @Value("${app.jwt.secret}")
//     private String jwtSecret;
    
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, 
//                                     HttpServletResponse response, 
//                                     FilterChain filterChain) throws ServletException, IOException {
        
//         // First, check for API Gateway headers
//         String userId = request.getHeader("X-User-Id");
//         String userEmail = request.getHeader("X-User-Email");
//         String rolesHeader = request.getHeader("X-User-Roles");
        
//         if (userId != null && userEmail != null) {
//             // Request came through API Gateway
//             List<SimpleGrantedAuthority> authorities = parseRoles(rolesHeader);
            
//             UsernamePasswordAuthenticationToken authentication = 
//                 new UsernamePasswordAuthenticationToken(userId, null, authorities);
            
//             SecurityContextHolder.getContext().setAuthentication(authentication);
//             log.debug("User {} authenticated via Gateway with roles: {}", userEmail, rolesHeader);
//         } else {
//             // Direct request - validate JWT token
//             String authHeader = request.getHeader("Authorization");
            
//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 try {
//                     String token = authHeader.substring(7);
                    
//                     // Validate token using JwtUtil
//                     if (JwtUtil.validateToken(token, jwtSecret)) {
//                         Claims claims = JwtUtil.extractAllClaims(token, jwtSecret);
                        
//                         String email = claims.getSubject();
//                         String userIdFromToken = claims.get("user_id", String.class);
//                         @SuppressWarnings("unchecked")
//                         List<String> roles = claims.get("roles", List.class);
                        
//                         log.info("Token validated for user: {}, roles: {}", email, roles);
                        
//                         // Convert roles to authorities with ROLE_ prefix
//                         List<SimpleGrantedAuthority> authorities = roles.stream()
//                             .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                             .collect(Collectors.toList());
                        
//                         UsernamePasswordAuthenticationToken authentication = 
//                             new UsernamePasswordAuthenticationToken(userIdFromToken, null, authorities);
                        
//                         SecurityContextHolder.getContext().setAuthentication(authentication);
//                         log.info("User {} authenticated directly with authorities: {}", email, authorities);
//                     } else {
//                         log.error("JWT validation failed: Invalid or expired token");
//                         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                         response.setContentType("application/json");
//                         response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
//                         return;
//                     }
                    
//                 } catch (Exception e) {
//                     log.error("JWT validation failed with exception: {}", e.getMessage(), e);
//                     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                     response.setContentType("application/json");
//                     response.getWriter().write("{\"error\": \"Invalid or expired token: " + e.getMessage() + "\"}");
//                     return;
//                 }
//             }
//         }
        
//         filterChain.doFilter(request, response);
//     }
    
//     private List<SimpleGrantedAuthority> parseRoles(String rolesHeader) {
//         if (rolesHeader == null || rolesHeader.isEmpty()) {
//             return List.of();
//         }
        
//         return List.of(rolesHeader.replace("[", "").replace("]", "").split(","))
//                 .stream()
//                 .map(String::trim)
//                 .map(role -> {
//                     if (!role.startsWith("ROLE_")) {
//                         return new SimpleGrantedAuthority("ROLE_" + role);
//                     }
//                     return new SimpleGrantedAuthority(role);
//                 })
//                 .collect(Collectors.toList());
//     }
// }



package com.healthapp.doctor.security;

import com.healthapp.shared.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidator extends OncePerRequestFilter {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Check for API Gateway headers
        String userId = request.getHeader("X-User-Id");
        String userEmail = request.getHeader("X-User-Email");
        String rolesHeader = request.getHeader("X-User-Roles");
        
        if (userId != null && userEmail != null) {
            // Request came through API Gateway
            List<SimpleGrantedAuthority> authorities = parseRoles(rolesHeader);
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("User {} authenticated via Gateway", userEmail);
        } else {
            // Direct request - validate JWT token
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    
                    if (JwtUtil.validateToken(token, jwtSecret)) {
                        Claims claims = JwtUtil.extractAllClaims(token, jwtSecret);
                        
                        String email = claims.getSubject();
                        String userIdFromToken = claims.get("userId", String.class);
                        
                        // ✅ CORRECTION: Gérer le cas où roles est null
                        @SuppressWarnings("unchecked")
                        List<String> roles = claims.get("roles", List.class);
                        
                        if (roles == null || roles.isEmpty()) {
                            log.warn("⚠️ No roles found in token for user: {}", email);
                            roles = new ArrayList<>();
                        }
                        
                        log.info("✅ Token validated for user: {}, roles: {}", email, roles);
                        
                        // Convert roles to authorities with ROLE_ prefix
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userIdFromToken, null, authorities);
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("✅ User {} authenticated with authorities: {}", email, authorities);
                    } else {
                        log.error("❌ JWT validation failed");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                        return;
                    }
                    
                } catch (Exception e) {
                    log.error("❌ JWT validation error: {}", e.getMessage(), e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid or expired token: " + e.getMessage() + "\"}");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private List<SimpleGrantedAuthority> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isEmpty()) {
            return List.of();
        }
        
        return List.of(rolesHeader.replace("[", "").replace("]", "").split(","))
                .stream()
                .map(String::trim)
                .map(role -> {
                    if (!role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority("ROLE_" + role);
                    }
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/doctors/register") ||
               path.startsWith("/api/doctors/login") ||
               path.startsWith("/api/doctors/health") ||
               path.startsWith("/actuator");
    }
}