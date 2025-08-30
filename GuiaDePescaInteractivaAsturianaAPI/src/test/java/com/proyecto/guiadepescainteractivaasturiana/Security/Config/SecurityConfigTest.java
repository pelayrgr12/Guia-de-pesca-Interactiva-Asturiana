package com.proyecto.guiadepescainteractivaasturiana.Security.Config;

import com.proyecto.guiadepescainteractivaasturiana.Security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private CustomUserDetailsService userDetailsService;
    private JwtFilter jwtFilter;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(CustomUserDetailsService.class);
        jwtFilter = mock(JwtFilter.class);
        securityConfig = new SecurityConfig(userDetailsService, jwtFilter);
    }

    @Test
    void passwordEncoder_ReturnBCryptEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword);

        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void userDetailsService_ReturnInjectedService() {
        assertEquals(userDetailsService, securityConfig.userDetailsService());
    }

    @Test
    void corsConfigurationSource_AllowsAllOriginsPattern() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        CorsConfiguration config = source.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(config);

        List<String> allowedOriginPatterns = config.getAllowedOriginPatterns();
        assertNotNull(allowedOriginPatterns);
        assertTrue(allowedOriginPatterns.contains("*"));

        assertTrue(config.getAllowCredentials());
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedHeaders().contains("*"));
    }


    @Test
    void authenticationManager_BeCreated() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);
        assertEquals(mockManager, result);
    }
}
