package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.UsuarioDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Security.Config.JwtUtil;
import com.proyecto.guiadepescainteractivaasturiana.Service.UsuarioService;
import com.proyecto.guiadepescainteractivaasturiana.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST responsable de las operaciones de autenticación:
 * registro, login y recuperación de contraseña.
 * <p>
 * No realiza cambios en la lógica base, sólo añade documentación.
 * </p>
 *
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;

    /**
     * Registra un nuevo usuario en el sistema y devuelve un token JWT.
     *
     * @param usuarioDTO objeto DTO con los datos necesarios para crear un usuario
     * @return ResponseEntity con un mapa JSON que incluye el token generado
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UsuarioDTO usuarioDTO) {

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        usuario.setHabilitado(true);
        usuario = usuarioService.registrarUsuario(usuario, usuarioDTO.getContrasena());
        String token = jwtUtil.generateToken(usuario.getNombre(), "USER");
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    /**
     * Autentica un usuario mediante correo y contraseña.
     * Si las credenciales son válidas y el usuario está habilitado,
     * devuelve un token JWT.
     *
     * @param loginData un mapa con clave "correo" y "password"
     * @return ResponseEntity con el token o error (estado 401) según el resultado
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginData) {
        String correo = loginData.get("correo");
        String password = loginData.get("password");

        if (usuarioService.validarCredenciales(correo, password)) {
            Usuario usuario = usuarioService.findByCorreo(correo).get();
            String rol = usuario.getRol().getRol();
            String token = jwtUtil.generateToken(correo, rol);
            if (!usuario.isHabilitado()) {
                return ResponseEntity.status(401).body(Map.of("error", "Usuario deshabilitado"));
            }
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña:
     * valida el correo y, si corresponde a un usuario registrado,
     * envía un correo de recuperación.
     *
     * @param correoString correo en formato JSON plano (p.ej. "\"test@example.com\"")
     * @return ResponseEntity con mensaje de éxito o error (400 o 404)
     */
    @PostMapping("recuperarPassword")
    public ResponseEntity<String> recuperar(@RequestBody String correoString) {
        String correo = correoString.replace("\"", "");
        if (!correo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.badRequest().body("Correo inválido");
        }

        List<Usuario> listaUsuarios = usuarioService.findAll();
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                emailService.enviarCorreoRecuperacion(correo, usuario);
                return ResponseEntity.ok("Correo enviado a " + correo);
            }
        }
        return ResponseEntity.status(404).body("No se encuentra el correo: " + correo);
    }
}
