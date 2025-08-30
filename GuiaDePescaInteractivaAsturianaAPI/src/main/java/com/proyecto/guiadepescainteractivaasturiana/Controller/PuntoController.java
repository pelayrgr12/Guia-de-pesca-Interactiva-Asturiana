package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.PuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Service.PuntoService;
import com.proyecto.guiadepescainteractivaasturiana.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para la gestión de puntos de pesca del usuario autenticado.
 * <p>
 * Permite ver, modificar, crear y eliminar puntos asociados al usuario.
 * La lógica existente se mantiene sin cambios, añadiendo únicamente documentación.
 * </p>
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/punto")
public class PuntoController {

    @Autowired
    private PuntoService puntoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Devuelve los puntos de pesca asociados al usuario autenticado.
     *
     * @param userDetails información del usuario autenticado
     * @return ResponseEntity con conjunto de PuntoDTO
     */
    @GetMapping("/mis-puntos")
    public ResponseEntity<HashSet<PuntoDTO>> getMisPuntos(@AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);
        List<Punto> puntos = puntoService.findAllByIdUsuario(usuario.getIdUsuario());
        HashSet<PuntoDTO> puntosDTO = new HashSet<>();
        for (Punto punto : puntos) {
            puntosDTO.add(new PuntoDTO(punto.getIdPunto(), punto.getLatitud(), punto.getLongitud(),
                    punto.getNombre(), punto.getDescripcion()));
        }
        return ResponseEntity.ok(puntosDTO);
    }

    /**
     * Modifica un punto existente del usuario.
     *
     * @param puntoActualizado datos actualizados del punto, incluyendo su ID
     * @param userDetails      información del usuario autenticado
     * @return ResponseEntity con mensaje o código de estado según resultado
     */
    @PutMapping("/modificar")
    public ResponseEntity<String> modificarPunto(@RequestBody Punto puntoActualizado,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);
        List<Punto> puntos = puntoService.findAllByIdUsuario(usuario.getIdUsuario());
        for (Punto punto : puntos) {
            if (punto.getIdPunto() != puntoActualizado.getIdPunto()
                    && punto.getNombre().equalsIgnoreCase(puntoActualizado.getNombre())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ya tienes otro punto con ese nombre.");
            }
        }
        boolean actualizado = puntoService.modificarPunto(puntoActualizado, usuario);
        if (actualizado) {
            return ResponseEntity.ok("Punto modificado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso o el punto no existe");
        }
    }

    /**
     * Crea un nuevo punto de pesca para el usuario autenticado.
     *
     * @param puntoDTO   datos del nuevo punto (DTO)
     * @param userDetails información del usuario autenticado
     * @return ResponseEntity con mensaje de creación o error adecuado
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> guardarPunto(@RequestBody PuntoDTO puntoDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);
        try {
            puntoService.guardarPunto(puntoDTO, usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Punto guardado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error inesperado al guardar el punto"));
        }
    }

    /**
     * Elimina un punto existente del usuario autenticado.
     *
     * @param puntoDTO    identifica el punto a eliminar (solo se necesita el ID)
     * @param userDetails información del usuario autenticado
     * @return ResponseEntity con HTTP 200 si fue eliminado o 404 si no se encontró
     */
    @DeleteMapping
    public ResponseEntity<?> deletePunto(@RequestBody PuntoDTO puntoDTO,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        Usuario usuario = usuarioService.getUsuarioByCorreo(correo);
        boolean deleted = puntoService.deletePunto(puntoDTO, usuario);
        return deleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
