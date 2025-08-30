package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.HabilitarUserDTO;
import com.proyecto.guiadepescainteractivaasturiana.DTo.UsuarioListDTO;
import com.proyecto.guiadepescainteractivaasturiana.DTo.UsuarioPutDTo;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST que gestiona operaciones relacionadas con los usuarios:
 * listado, habilitación, datos del usuario actual y actualización de perfil.
 * <p>
 * Conserva toda la funcionalidad original sin alterar lógicas del negocio.
 * </p>
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista de todos los usuarios en formato DTO.
     *
     * @return lista de {@link UsuarioListDTO} representando todos los usuarios
     */
    @GetMapping
    public List<UsuarioListDTO> findAll() {
        List<Usuario> list = usuarioService.findAll();
        List<UsuarioListDTO> listDTOS = new ArrayList<>();
        for (Usuario usuario : list) {
            listDTOS.add(new UsuarioListDTO(
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    usuario.getFechaNacimiento(),
                    usuario.isHabilitado()
            ));
        }
        return listDTOS;
    }

    /**
     * Habilita o deshabilita un usuario según el DTO recibido.
     *
     * @param dto contiene el ID del usuario y el estado habilitado a aplicar
     * @return ResponseEntity con mensaje de éxito o error 404 si no existe el usuario
     */
    @PatchMapping("/habilitar")
    public ResponseEntity<String> cambiarHabilitado(@RequestBody HabilitarUserDTO dto) {
        boolean actualizado = usuarioService.actualizarHabilitado(dto.getId(), dto.isHabilitado());
        if (actualizado) {
            return ResponseEntity.ok("Estado actualizado correctamente");
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    /**
     * Obtiene los datos del usuario autenticado actualmente.
     *
     * @return ResponseEntity con {@link UsuarioListDTO} del usuario o 404 si no se encuentra
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioListDTO> getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(username);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            UsuarioListDTO dto = new UsuarioListDTO(
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    usuario.getFechaNacimiento(),
                    usuario.isHabilitado()
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    /**
     * Actualiza los datos del usuario autenticado usando el DTO enviado.
     *
     * @param dto instancias de datos a actualizar (nombre, correo, etc.)
     * @return ResponseEntity con mensaje de éxito o código de error según excepción lanzada
     */
    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizarUsuario(@RequestBody UsuarioPutDTo dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        try {
            usuarioService.actualizarDatosUsuario(correo, dto);
            return ResponseEntity.ok("Usuario actualizado correctamente");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
