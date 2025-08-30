package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void enviarCorreoRecuperacion() {
        // Arrange
        String correo = "usuario@correo.com";
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);

        when(passwordEncoder.encode(anyString())).thenReturn("hashficticio");

        emailService.enviarCorreoRecuperacion(correo, usuario);

        ArgumentCaptor<SimpleMailMessage> mensajeCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mensajeCaptor.capture());

        SimpleMailMessage mensajeEnviado = mensajeCaptor.getValue();
        assertEquals(correo, mensajeEnviado.getTo()[0]);
        assertEquals("Recuperación de contraseña", mensajeEnviado.getSubject());
        assertTrue(mensajeEnviado.getText().contains("Tu nueva contraseña temporal es:"));

        assertEquals("hashficticio", usuario.getContrasena());
        verify(usuarioService).actualizarUsuario(usuario);
    }
}
