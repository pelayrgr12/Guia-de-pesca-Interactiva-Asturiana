package com.proyecto.guiadepescainteractivaasturiana.Security;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UsuarioRepository usuarioRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        customUserDetailsService = new CustomUserDetailsService(usuarioRepository);
    }

    @Test
    void loadUserByUsername_ReturnUserDetails_whenUserExists() {
        // Arrange
        Rol rol = new Rol();
        rol.setRol("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setCorreo("correo@ejemplo.com");
        usuario.setContrasena("contrasena123");
        usuario.setRol(rol);

        when(usuarioRepository.findByCorreo("correo@ejemplo.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("correo@ejemplo.com");

        // Assert
        assertEquals("correo@ejemplo.com", userDetails.getUsername());
        assertEquals("contrasena123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ThrowException_whenUserDoesNotExist() {
        when(usuarioRepository.findByCorreo("inexistente@correo.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("inexistente@correo.com"));
    }
}
