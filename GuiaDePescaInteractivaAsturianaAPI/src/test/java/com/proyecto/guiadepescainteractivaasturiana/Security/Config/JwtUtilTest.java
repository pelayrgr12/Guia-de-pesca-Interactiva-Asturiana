package com.proyecto.guiadepescainteractivaasturiana.Security.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // ✅ Clave segura con más de 32 caracteres
    private final String rawSecret = "clave-super-secreta-para-testing-jwt-123456";
    private final String secretKey = Base64.getEncoder().encodeToString(rawSecret.getBytes());

    private final long expirationTime = 1000 * 60 * 60; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Establecer los valores mediante reflexión
        try {
            var secretField = JwtUtil.class.getDeclaredField("secretKey");
            secretField.setAccessible(true);
            secretField.set(jwtUtil, secretKey);

            var expirationField = JwtUtil.class.getDeclaredField("expirationTime");
            expirationField.setAccessible(true);
            expirationField.set(jwtUtil, expirationTime);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar JwtUtil en pruebas", e);
        }
    }

    @Test
    void generateToken() {
        String correo = "test@correo.com";
        String rol = "USER";

        String token = jwtUtil.generateToken(correo, rol);

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        assertEquals(correo, claims.getSubject());
        assertEquals(rol, claims.get("rol"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void extractUsername_ReturnCorrectUsername() {
        String correo = "usuario@correo.com";
        String token = jwtUtil.generateToken(correo, "ADMIN");

        String resultado = jwtUtil.extractUsername(token);
        assertEquals(correo, resultado);
    }

    @Test
    void validateToken_ReturnTrue() {
        String correo = "user@example.com";
        String token = jwtUtil.generateToken(correo, "USER");

        assertTrue(jwtUtil.validateToken(token, correo));
    }

    @Test
    void validateToken_ReturnFalseForInvalidUsername() {
        String token = jwtUtil.generateToken("correo@ejemplo.com", "USER");

        assertFalse(jwtUtil.validateToken(token, "otro@correo.com"));
    }

    @Test
    void isTokenExpired_shouldReturnTrueWhenTokenExpired() throws Exception {
        // Forzar expiración
        var field = JwtUtil.class.getDeclaredField("expirationTime");
        field.setAccessible(true);
        field.set(jwtUtil, -1000L); // Token ya expirado

        String token = jwtUtil.generateToken("exp@correo.com", "USER");

        // Comprobar que el token se considera inválido
        boolean resultado = false;
        try {
            resultado = jwtUtil.validateToken(token, "exp@correo.com");
        } catch (Exception e) {
            // En caso de excepción esperada, también es válido
            assertTrue(e instanceof io.jsonwebtoken.ExpiredJwtException);
            return;
        }

        // Alternativamente, si no lanza excepción, simplemente que sea falso
        assertFalse(resultado);
    }

}
