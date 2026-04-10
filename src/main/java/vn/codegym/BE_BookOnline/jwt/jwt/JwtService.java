package vn.codegym.BE_BookOnline.jwt.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.codegym.BE_BookOnline.exception.JwtAuthenticationException;
import vn.codegym.BE_BookOnline.model.Role;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.service.UserSecurityService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// tạo Jwt toke va gan thong tin use vao token de FE su dung, giai ma token
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final UserSecurityService userSecurityService;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // tao jwt dua tren username(tao thong tin can tra ve cho FE khi dang nhap thanh cong)
    public String generateToken(String email){
        Map<String, Object> claims = new HashMap<>();//playload cua JWT
        User user = userSecurityService.findByUserEmail(email);
        claims.put("id", user.getId());
        claims.put("avatar", user.getAvatar());
        claims.put("fullName", user.getFullName());
        claims.put("enabled", user.isEnabled());

        List <Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()//tao luong role
                .map(Role::getNameRole)// role -> String
                .toList();// gom thanh list
        claims.put("roles", roleNames);
        return createToken(claims, email);
    }
    // tao token
    private String createToken(Map<String, Object> claims, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    //lay email tu token
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //lay UserId tu token
    public Long extractUserId(String token){
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    //lay role tu token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // lay expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // lay claims với resolver
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // lay tat ca claims từ token
    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new JwtAuthenticationException("Token has expired", e);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid token format", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new JwtAuthenticationException("Token claims are empty", e);
        }
    }

    private Boolean isTokenExpired(String token){
        try{
            return extractExpiration(token).before(new Date());
        }catch (Exception e){
            return true;
        }
    }

    // validate toke
    public Boolean validateToken(String token){
        try{
            extractAllClaims(token);
            return !isTokenExpired(token);
        }catch (Exception e){
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
