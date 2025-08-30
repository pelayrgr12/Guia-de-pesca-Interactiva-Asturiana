package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.MedidaDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Medida;
import com.proyecto.guiadepescainteractivaasturiana.Service.MedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controlador REST que gestiona las operaciones relacionadas con las medidas de pesca:
 * subida y recuperación de imágenes, creación, actualización, consulta y eliminación de medidas.
 * <p>
 * Inserta documentación JavaDoc sin modificar la implementación original.
 * </p>
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/medida")
public class MedidaController {

    @Autowired
    private MedidaService medidaService;

    private final String rutaMedidas = "/app/images/medidas/";
    private final String URL_IMAGENES = "https://192.168.7.156/images/medidas/";

    /**
     * Endpoint para subir una imagen asociada a una medida.
     *
     * @param imagen archivo en formato multipart para la medida
     * @return ResponseEntity con mensaje de éxito o error según resultado del servicio
     */
    @PostMapping("/imagen")
    public ResponseEntity<String> subirImagenMedida(@RequestParam("imagen") MultipartFile imagen) {
        return medidaService.subirImagenMedida(imagen);
    }

    /**
     * Crea una nueva medida en el sistema.
     *
     * @param dto objeto MedidaDTO con datos de la nueva medida
     * @return ResponseEntity con el MedidaDTO creado o error 400 si es inválido
     */
    @PostMapping
    public ResponseEntity<MedidaDTO> crearMedida(@RequestBody MedidaDTO dto) {
        MedidaDTO medidaDTO = medidaService.guardarMedida(dto);
        if (medidaDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(medidaDTO);
    }

    /**
     * Actualiza una medida existente identificada por su ID.
     *
     * @param id ID de la medida a actualizar
     * @param medidaActualizada instancia de Medida con los nuevos datos
     * @return ResponseEntity con mensaje de éxito o error 404 si no se encuentra
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarMedida(@PathVariable int id,
                                                   @RequestBody Medida medidaActualizada) {
        boolean actualizada = medidaService.actualizarMedida(id, medidaActualizada);
        if (actualizada) {
            return ResponseEntity.ok("Medida actualizada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medida no encontrada");
        }
    }

    /**
     * Devuelve la imagen asociada a una medida por su nombre.
     *
     * @param nombre nombre del archivo de la imagen
     * @return ResponseEntity con el recurso de imagen o errores HTTP
     */
    @GetMapping("/imagen/{nombre}")
    public ResponseEntity<Resource> getImagenMedida(@PathVariable String nombre) {
        try {
            Path ruta = Paths.get(rutaMedidas).resolve(nombre).normalize();
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todas las medidas disponibles como lista de DTO.
     *
     * @return ResponseEntity con la lista de MedidaDTO
     */
    @GetMapping
    public ResponseEntity<List<MedidaDTO>> getMedida() {
        List<Medida> medidas = medidaService.obtenerTodas();
        List<MedidaDTO> medidasDTO = medidas.stream().map(m -> {
            MedidaDTO dto = new MedidaDTO();
            dto.setIdMedida(m.getIdMedida());
            dto.setNombreComun(m.getNombreComun());
            dto.setNombreCientifico(m.getNombreCientifico());
            dto.setTallaMinima(m.getTallaMinima());
            dto.setImagen(m.getImagen());
            dto.setIdTipo(m.getTipoAnimal().getIdTipo());
            return dto;
        }).toList();
        return ResponseEntity.ok(medidasDTO);
    }

    /**
     * Elimina una medida junto con su imagen del sistema.
     *
     * @param id ID de la medida a eliminar
     * @return ResponseEntity con mensaje de éxito o error 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMedida(@PathVariable int id) {
        Medida medida = medidaService.obtenerPorId(id);
        if (medida == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medida no encontrada");
        }
        try {
            Path rutaImagen = Paths.get(rutaMedidas).resolve(medida.getImagen());
            Files.deleteIfExists(rutaImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        medidaService.eliminarMedida(id);
        return ResponseEntity.ok("Medida eliminada correctamente");
    }
}
