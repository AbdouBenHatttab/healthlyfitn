// // // // // package com.healthapp.shared.util;

// // // // // import io.jsonwebtoken.Claims;
// // // // // import io.jsonwebtoken.Jwts;
// // // // // import io.jsonwebtoken.security.Keys;

// // // // // import javax.crypto.SecretKey;
// // // // // import java.nio.charset.StandardCharsets;
// // // // // import java.util.Date;

// // // // // /**
// // // // //  * Utilitaire JWT partagé entre les microservices
// // // // //  * Permet de valider les tokens sans recréer la logique partout
// // // // //  */
// // // // // public class JwtUtil {

// // // // //     public static Claims extractAllClaims(String token, String secret) {
// // // // //         SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

// // // // //         return Jwts.parserBuilder()
// // // // //                 .setSigningKey(key)
// // // // //                 .build()
// // // // //                 .parseClaimsJws(token)
// // // // //                 .getBody();
// // // // //     }

// // // // //     public static String extractUsername(String token, String secret) {
// // // // //         return extractAllClaims(token, secret).getSubject();
// // // // //     }

// // // // //     public static String extractUserId(String token, String secret) {
// // // // //         return extractAllClaims(token, secret).get("user_id", String.class);
// // // // //     }

// // // // //     public static boolean isTokenExpired(String token, String secret) {
// // // // //         return extractAllClaims(token, secret)
// // // // //                 .getExpiration()
// // // // //                 .before(new Date());
// // // // //     }

// // // // //     public static boolean isTokenValid(String token, String username, String secret) {
// // // // //         final String extractedUsername = extractUsername(token, secret);
// // // // //         return extractedUsername.equals(username) && !isTokenExpired(token, secret);
// // // // //     }
// // // // // }
// // // // package com.healthapp.shared.util;

// // // // import io.jsonwebtoken.Claims;
// // // // import io.jsonwebtoken.Jwts;
// // // // import io.jsonwebtoken.SignatureAlgorithm;
// // // // import io.jsonwebtoken.security.Keys;

// // // // import javax.crypto.SecretKey;
// // // // import java.nio.charset.StandardCharsets;
// // // // import java.util.Date;
// // // // import java.util.Map;

// // // // /**
// // // //  * Utilitaire JWT partagé entre les microservices
// // // //  * ✅ Peut générer et valider des tokens JWT
// // // //  */
// // // // public class JwtUtil {

// // // //     // 🔹 --- EXISTANT ---
// // // //     public static Claims extractAllClaims(String token, String secret) {
// // // //         SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
// // // //         return Jwts.parserBuilder()
// // // //                 .setSigningKey(key)
// // // //                 .build()
// // // //                 .parseClaimsJws(token)
// // // //                 .getBody();
// // // //     }

// // // //     public static String extractUsername(String token, String secret) {
// // // //         return extractAllClaims(token, secret).getSubject();
// // // //     }

// // // //     public static String extractUserId(String token, String secret) {
// // // //         return extractAllClaims(token, secret).get("user_id", String.class);
// // // //     }

// // // //     public static boolean isTokenExpired(String token, String secret) {
// // // //         return extractAllClaims(token, secret)
// // // //                 .getExpiration()
// // // //                 .before(new Date());
// // // //     }

// // // //     public static boolean isTokenValid(String token, String username, String secret) {
// // // //         final String extractedUsername = extractUsername(token, secret);
// // // //         return extractedUsername.equals(username) && !isTokenExpired(token, secret);
// // // //     }

// // // //     // 🔹 --- NOUVEAU ---
// // // //     /**
// // // //      * Génère un token JWT signé avec une clé secrète.
// // // //      */
// // // //     public static String generateToken(Map<String, Object> claims, String subject, Long expirationMillis, String secret) {
// // // //         SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
// // // //         Date now = new Date();
// // // //         Date expiry = new Date(now.getTime() + expirationMillis);

// // // //         return Jwts.builder()
// // // //                 .setClaims(claims)
// // // //                 .setSubject(subject)
// // // //                 .setIssuedAt(now)
// // // //                 .setExpiration(expiry)
// // // //                 .signWith(key, SignatureAlgorithm.HS256)
// // // //                 .compact();
// // // //     }
// // // // }
// // // package com.healthapp.shared.util;

// // // import io.jsonwebtoken.Claims;
// // // import io.jsonwebtoken.Jwts;
// // // import io.jsonwebtoken.SignatureAlgorithm;
// // // import io.jsonwebtoken.security.Keys;

// // // import javax.crypto.SecretKey;
// // // import java.nio.charset.StandardCharsets;
// // // import java.util.Date;
// // // import java.util.Map;

// // // /**
// // //  * JwtUtil - Shared JWT utility for all microservices
// // //  * Handles token generation and validation
// // //  */
// // // public class JwtUtil {

// // //     /**
// // //      * Extract all claims from a JWT token
// // //      */
// // //     public static Claims extractAllClaims(String token, String secret) {
// // //         SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
// // //         return Jwts.parserBuilder()
// // //                 .setSigningKey(key)
// // //                 .build()
// // //                 .parseClaimsJws(token)
// // //                 .getBody();
// // //     }

// // //     /**
// // //      * Extract username from token
// // //      */
// // //     public static String extractUsername(String token, String secret) {
// // //         return extractAllClaims(token, secret).getSubject();
// // //     }

// // //     /**
// // //      * Extract userId from token
// // //      */
// // //     public static String extractUserId(String token, String secret) {
// // //         return extractAllClaims(token, secret).get("user_id", String.class);
// // //     }

// // //     /**
// // //      * Check if token is expired
// // //      */
// // //     public static boolean isTokenExpired(String token, String secret) {
// // //         return extractAllClaims(token, secret)
// // //                 .getExpiration()
// // //                 .before(new Date());
// // //     }

// // //     /**
// // //      * Validate token
// // //      */
// // //     public static boolean isTokenValid(String token, String username, String secret) {
// // //         final String extractedUsername = extractUsername(token, secret);
// // //         return extractedUsername.equals(username) && !isTokenExpired(token, secret);
// // //     }

// // //     /**
// // //      * Generate JWT token with claims
// // //      * @param claims Map of custom claims
// // //      * @param subject Token subject (usually email or username)
// // //      * @param expirationMillis Expiration time in milliseconds
// // //      * @param secret Secret key for signing
// // //      * @return Generated JWT token
// // //      */
// // //     public static String generateToken(Map<String, Object> claims, String subject, Long expirationMillis, String secret) {
// // //         SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
// // //         Date now = new Date();
// // //         Date expiry = new Date(now.getTime() + expirationMillis);

// // //         return Jwts.builder()
// // //                 .setClaims(claims)
// // //                 .setSubject(subject)
// // //                 .setIssuedAt(now)
// // //                 .setExpiration(expiry)
// // //                 .signWith(key, SignatureAlgorithm.HS256)
// // //                 .compact();
// // //     }
// // // }


// // package com.healthapp.shared.util;

// // import io.jsonwebtoken.Claims;
// // import io.jsonwebtoken.Jwts;
// // import io.jsonwebtoken.SignatureAlgorithm;

// // import javax.crypto.SecretKey;
// // import javax.crypto.spec.SecretKeySpec;
// // import java.nio.charset.StandardCharsets;
// // import java.util.Date;
// // import java.util.Map;

// // /**
// //  * JwtUtil - Shared JWT utility for all microservices
// //  * Handles token generation and validation
// //  */
// // public class JwtUtil {

// //     /**
// //      * Create SecretKey from string secret
// //      */
// //     private static SecretKey getSigningKey(String secret) {
// //         return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
// //     }

// //     /**
// //      * Extract all claims from a JWT token
// //      */
// //     public static Claims extractAllClaims(String token, String secret) {
// //         SecretKey key = getSigningKey(secret);
// //         return Jwts.parser()
// //                 .setSigningKey(key)
// //                 .parseClaimsJws(token)
// //                 .getBody();
// //     }

// //     /**
// //      * Extract username from token
// //      */
// //     public static String extractUsername(String token, String secret) {
// //         return extractAllClaims(token, secret).getSubject();
// //     }

// //     /**
// //      * Extract userId from token
// //      */
// //     public static String extractUserId(String token, String secret) {
// //         return extractAllClaims(token, secret).get("user_id", String.class);
// //     }

// //     /**
// //      * Extract roles from token
// //      */
// //     @SuppressWarnings("unchecked")
// //     public static java.util.List<String> extractRoles(String token, String secret) {
// //         return extractAllClaims(token, secret).get("roles", java.util.List.class);
// //     }

// //     /**
// //      * Check if token is expired
// //      */
// //     public static boolean isTokenExpired(String token, String secret) {
// //         return extractAllClaims(token, secret)
// //                 .getExpiration()
// //                 .before(new Date());
// //     }

// //     /**
// //      * Validate token
// //      */
// //     public static boolean isTokenValid(String token, String username, String secret) {
// //         final String extractedUsername = extractUsername(token, secret);
// //         return extractedUsername.equals(username) && !isTokenExpired(token, secret);
// //     }

// //     /**
// //      * Validate token (without username check)
// //      */
// //     public static boolean validateToken(String token, String secret) {
// //         try {
// //             extractAllClaims(token, secret);
// //             return !isTokenExpired(token, secret);
// //         } catch (Exception e) {
// //             return false;
// //         }
// //     }

// //     /**
// //      * Generate JWT token with claims
// //      * @param claims Map of custom claims
// //      * @param subject Token subject (usually email or username)
// //      * @param expirationMillis Expiration time in milliseconds
// //      * @param secret Secret key for signing
// //      * @return Generated JWT token
// //      */
// //     public static String generateToken(Map<String, Object> claims, String subject, Long expirationMillis, String secret) {
// //         SecretKey key = getSigningKey(secret);
// //         Date now = new Date();
// //         Date expiry = new Date(now.getTime() + expirationMillis);

// //         return Jwts.builder()
// //                 .setClaims(claims)
// //                 .setSubject(subject)
// //                 .setIssuedAt(now)
// //                 .setExpiration(expiry)
// //                 .signWith(key, SignatureAlgorithm.HS512)
// //                 .compact();
// //     }
// // }


// package com.healthapp.shared.util;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;

// import javax.crypto.SecretKey;
// import java.nio.charset.StandardCharsets;
// import java.util.Date;
// import java.util.Map;

// /**
//  * JwtUtil - Shared JWT utility for all microservices
//  * Compatible with jjwt 0.12.x+
//  */
// public class JwtUtil {

//     /**
//      * Create SecretKey from string secret
//      */
//     private static SecretKey getSigningKey(String secret) {
//         return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//     }

//     /**
//      * Extract all claims from a JWT token
//      * Uses parserBuilder() for jjwt 0.12.x+ compatibility
//      */
//     public static Claims extractAllClaims(String token, String secret) {
//         SecretKey key = getSigningKey(secret);
//         return Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     /**
//      * Extract username from token
//      */
//     public static String extractUsername(String token, String secret) {
//         return extractAllClaims(token, secret).getSubject();
//     }

//     /**
//      * Extract userId from token
//      */
//     public static String extractUserId(String token, String secret) {
//         return extractAllClaims(token, secret).get("user_id", String.class);
//     }

//     /**
//      * Extract roles from token
//      */
//     @SuppressWarnings("unchecked")
//     public static java.util.List<String> extractRoles(String token, String secret) {
//         return extractAllClaims(token, secret).get("roles", java.util.List.class);
//     }

//     /**
//      * Check if token is expired
//      */
//     public static boolean isTokenExpired(String token, String secret) {
//         return extractAllClaims(token, secret)
//                 .getExpiration()
//                 .before(new Date());
//     }

//     /**
//      * Validate token (with username check)
//      */
//     public static boolean isTokenValid(String token, String username, String secret) {
//         final String extractedUsername = extractUsername(token, secret);
//         return extractedUsername.equals(username) && !isTokenExpired(token, secret);
//     }

//     /**
//      * Validate token (without username check)
//      */
//     public static boolean validateToken(String token, String secret) {
//         try {
//             extractAllClaims(token, secret);
//             return !isTokenExpired(token, secret);
//         } catch (Exception e) {
//             return false;
//         }
//     }

//     /**
//      * Generate JWT token with claims
//      * @param claims Map of custom claims
//      * @param subject Token subject (usually email or username)
//      * @param expirationMillis Expiration time in milliseconds
//      * @param secret Secret key for signing
//      * @return Generated JWT token
//      */
//     public static String generateToken(Map<String, Object> claims, String subject, Long expirationMillis, String secret) {
//         SecretKey key = getSigningKey(secret);
//         Date now = new Date();
//         Date expiry = new Date(now.getTime() + expirationMillis);

//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setSubject(subject)
//                 .setIssuedAt(now)
//                 .setExpiration(expiry)
//                 .signWith(key, SignatureAlgorithm.HS256)
//                 .compact();
//     }
// }


package com.healthapp.shared.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JwtUtil - Shared JWT utility for all microservices
 * ✅ Compatible with JJWT 0.12.3
 */
public class JwtUtil {

    /**
     * Create SecretKey from string secret
     */
    private static SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extract all claims from a JWT token
     * ✅ FIXED: Uses .parser() instead of .parserBuilder() for JJWT 0.12.3
     */
    public static Claims extractAllClaims(String token, String secret) {
        SecretKey key = getSigningKey(secret);
        return Jwts.parser()                    // ✅ CHANGED: parserBuilder() → parser()
                .verifyWith(key)                // ✅ CHANGED: setSigningKey() → verifyWith()
                .build()
                .parseSignedClaims(token)       // ✅ CHANGED: parseClaimsJws() → parseSignedClaims()
                .getPayload();                  // ✅ CHANGED: getBody() → getPayload()
    }

    /**
     * Extract username from token
     */
    public static String extractUsername(String token, String secret) {
        return extractAllClaims(token, secret).getSubject();
    }

    /**
     * Extract userId from token
     */
    public static String extractUserId(String token, String secret) {
        return extractAllClaims(token, secret).get("user_id", String.class);
    }

    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public static java.util.List<String> extractRoles(String token, String secret) {
        return extractAllClaims(token, secret).get("roles", java.util.List.class);
    }

    /**
     * Check if token is expired
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            return extractAllClaims(token, secret)
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return true; // Si erreur, considérer comme expiré
        }
    }

    /**
     * Validate token (with username check)
     */
    public static boolean isTokenValid(String token, String username, String secret) {
        try {
            final String extractedUsername = extractUsername(token, secret);
            return extractedUsername.equals(username) && !isTokenExpired(token, secret);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate token (without username check)
     */
    public static boolean validateToken(String token, String secret) {
        try {
            extractAllClaims(token, secret);
            return !isTokenExpired(token, secret);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate JWT token with claims
     * @param claims Map of custom claims
     * @param subject Token subject (usually email or username)
     * @param expirationMillis Expiration time in milliseconds
     * @param secret Secret key for signing
     * @return Generated JWT token
     */
    public static String generateToken(Map<String, Object> claims, String subject, Long expirationMillis, String secret) {
        SecretKey key = getSigningKey(secret);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .claims(claims)                 // ✅ CHANGED: setClaims() → claims()
                .subject(subject)               // ✅ CHANGED: setSubject() → subject()
                .issuedAt(now)                  // ✅ CHANGED: setIssuedAt() → issuedAt()
                .expiration(expiry)             // ✅ CHANGED: setExpiration() → expiration()
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}