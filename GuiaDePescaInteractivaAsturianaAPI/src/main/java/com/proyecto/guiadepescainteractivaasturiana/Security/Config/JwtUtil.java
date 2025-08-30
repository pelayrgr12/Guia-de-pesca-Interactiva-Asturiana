package com.proyecto.guiadepescainteractivaasturiana.Security.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import java.util.Date;


@Component
public class JwtUtil {

    /*
     NOTA. Se obtiene del properties la clave secreta y el tiempo de expiracion
     */
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}") // 👈 Obtiene el tiempo de expiración desde application.properties
    private long expirationTime;

    // Generar Token
    public String generateToken(String correo,String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Extraer Username del Token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Verificar si el Token es válido
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Obtener los claims (datos) del Token
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar si el Token ha expirado
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
