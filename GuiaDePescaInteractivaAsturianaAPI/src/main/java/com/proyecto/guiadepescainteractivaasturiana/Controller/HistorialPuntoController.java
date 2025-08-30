package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.HistorialPuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.DTo.ImagenHistorialDTo;
import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.ImagenHistorial;
import com.proyecto.guiadepescainteractivaasturiana.Service.HistorialPuntoService;
import com.proyecto.guiadepescainteractivaasturiana.Service.ImagenHistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST encargado de manejar las operaciones CRUD para los historiales
 * de puntos de pesca y la gestión de imágenes asociadas.
 * <p>
 * No se modifica la lógica de negocio existente, solo se añaden descripciones
 * de clases, métodos y parámetros para facilitar la generación de documentación JavaDoc.
 * </p>
 *
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/historial")
public class HistorialPuntoController {

    @Autowired
    private HistorialPuntoService historialPuntoService;

    @Autowired
    private ImagenHistorialService imagenHistorialService;

    /**
     * Obtiene la lista de historiales asociados a un punto identificado por su ID.
     *
     * @param id int ID del punto cuya lista de historiales se desea recuperar
     * @return ResponseEntity con una lista de HistorialPuntoDTO o código HTTP apropiado
     */
    @GetMapping("/punto/{id}")
    public ResponseEntity<?> getHistorialesPorPunto(@PathVariable int id) {
        List<HistorialPunto> lista = historialPuntoService.findByPunto(id);
        List<HistorialPuntoDTO> listaHistorial = new ArrayList<>();

        for (HistorialPunto historial : lista) {
            List<ImagenHistorialDTo> imagenesDTO = new ArrayList<>();
            if (historial.getImagenes() != null) {
                for (ImagenHistorial img : historial.getImagenes()) {
                    imagenesDTO.add(new ImagenHistorialDTo(img.getId(), img.getNombre()));
                }
            }
            HistorialPuntoDTO dto = new HistorialPuntoDTO(
                    historial.getIdHistorial(),
                    historial.getPunto().getIdPunto(),
                    historial.getFecha(),
                    historial.getDescripcion(),
                    imagenesDTO
            );
            listaHistorial.add(dto);
        }
        return ResponseEntity.ok(listaHistorial);
    }

    /**
     * Crea un nuevo historial para un punto de pesca.
     *
     * @param dto HistorialPuntoDTO con los datos del nuevo historial
     * @return ResponseEntity con el DTO del historial creado o un error 400 si no es válido
     */
    @PostMapping
    public ResponseEntity<HistorialPuntoDTO> agregarHistorial(@RequestBody HistorialPuntoDTO dto) {
        HistorialPuntoDTO resultado = historialPuntoService.guardarHistorial(dto);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    /**
     * Actualiza un historial existente identificado por su ID.
     *
     * @param id  int ID del historial que se desea actualizar
     * @param dto HistorialPuntoDTO con los nuevos valores; su ID será sobrescrito por el valor de la ruta
     * @return ResponseEntity con el DTO actualizado o un error 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<HistorialPuntoDTO> actualizarHistorial(
            @PathVariable int id, @RequestBody HistorialPuntoDTO dto) {
        dto.setIdHistorial(id);
        HistorialPuntoDTO actualizado = historialPuntoService.actualizarHistorial(dto);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un historial identificado por su ID.
     *
     * @param id int ID del historial que se desea eliminar
     * @return ResponseEntity sin contenido (204) si fue eliminado con éxito,
     *         o 404 si no se encontró
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable int id) {
        boolean eliminado = historialPuntoService.eliminarHistorial(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
