package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.UsuarioPutDTo;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleService roleService;

    @Test
    void registrarUsuario_deberiaAsignarRolYGuardar() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("nuevo@correo.com");
        String password = "Segura123!";

        Rol rol = new Rol();
        rol.setRol("USER");

        when(passwordEncoder.encode(password)).thenReturn("hashed-pass");
        when(roleService.obtenerRolPorNombre("USER")).thenReturn(rol);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario registrado = usuarioService.registrarUsuario(usuario, password);

        assertNotNull(registrado);
        assertEquals("hashed-pass", registrado.getContrasena());
        assertEquals("USER", registrado.getRol().getRol());
    }

    @Test
    void validarCredenciales_deberiaValidarCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@correo.com");
        usuario.setContrasena("hashed123");

        when(usuarioRepository.findByCorreo("test@correo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123", "hashed123")).thenReturn(true);

        boolean valido = usuarioService.validarCredenciales("test@correo.com", "123");
        assertTrue(valido);
    }

    @Test
    void loadUserByUsername_deberiaRetornarUserDetails() {
        Usuario usuario = new Usuario();
        usuario.setNombre("admin");
        usuario.setContrasena("hashed");

        when(usuarioRepository.findByNombre("admin")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("hashed", result.getPassword());
    }

    @Test
    void actualizarHabilitado_deberiaActualizarEstado() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setHabilitado(false);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        boolean actualizado = usuarioService.actualizarHabilitado(1, true);

        assertTrue(actualizado);
        assertTrue(usuario.isHabilitado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void actualizarDatosUsuario_deberiaCambiarCorreoYNombreYPassword() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("viejo@correo.com");
        usuario.setContrasena("hashed");
        usuario.setNombre("Viejo");

        UsuarioPutDTo dto = new UsuarioPutDTo();
        dto.setCorreo("nuevo@correo.com");
        dto.setNombre("Nuevo");
        dto.setContrasenaActual("123");
        dto.setNuevaContrasena("Segura123!");

        when(usuarioRepository.findByCorreo("viejo@correo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("Segura123!")).thenReturn("nuevahashed");

        usuarioService.actualizarDatosUsuario("viejo@correo.com", dto);

        assertEquals("Nuevo", usuario.getNombre());
        assertEquals("nuevo@correo.com", usuario.getCorreo());
        assertEquals("nuevahashed", usuario.getContrasena());
        verify(usuarioRepository).save(usuario);
    }
}
