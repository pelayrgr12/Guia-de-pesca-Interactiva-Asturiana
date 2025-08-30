package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void enviarCorreoRecuperacion(String correo, Usuario usuario) {
        String nuevaPassword = generarContrasenaAleatoria(8);
        String mensajeTexto = "Tu nueva contraseña temporal es: " + nuevaPassword;

        // Enviar correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText(mensajeTexto);
        mensaje.setFrom("soporteproyectomontenaranco@gmail.com");

        mailSender.send(mensaje);

        // Guardar la nueva contraseña cifrada
        usuario.setContrasena(passwordEncoder.encode(nuevaPassword));
        usuarioService.actualizarUsuario(usuario);
    }

    private String generarContrasenaAleatoria(int longitud) {
        String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minus = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiales = "!@#$%&*";

        String todos = mayus + minus + numeros + especiales;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        password.append(mayus.charAt(random.nextInt(mayus.length())));
        password.append(especiales.charAt(random.nextInt(especiales.length())));

        for (int i = 2; i < longitud; i++) {
            password.append(todos.charAt(random.nextInt(todos.length())));
        }

        return password.toString();
    }

}
