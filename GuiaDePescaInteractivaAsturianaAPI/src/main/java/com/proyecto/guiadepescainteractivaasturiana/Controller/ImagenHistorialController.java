package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.Service.ImagenHistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador REST que gestiona la subida, visualización y eliminación
 * de imágenes asociadas a historiales de pesca.
 * <p>
 * No modifica la lógica existente; añade documentación enriquecida con JavaDoc.
 * </p>
 *
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/imagen")
public class ImagenHistorialController {

    @Autowired
    private ImagenHistorialService imagenHistorialService;

    /**
     * Ruta de prueba para verificar que el endpoint de imagen funciona correctamente.
     *
     * @return ResponseEntity con mensaje de estado "Imagen endpoint OK"
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Imagen endpoint OK");
    }

    /**
     * Sube una imagen asociada a un historial específico.
     *
     * @param imagen      archivo multimedia representando la imagen
     * @param idHistorial identificador del historial al que se asocia la imagen
     * @return ResponseEntity con mensaje y código HTTP según resultado de la operación
     */
    @PostMapping("/subir")
    public ResponseEntity<String> subirImagen(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("idHistorial") Integer idHistorial
    ) {
        return imagenHistorialService.subirImagen(imagen, idHistorial);
    }

    /**
     * Obtiene una imagen por su nombre y la devuelve como recurso.
     *
     * @param nombre nombre del archivo de imagen
     * @return ResponseEntity con el recurso de imagen, encabezados adecuados, o error HTTP si no existe o ocurre fallo
     */
    @GetMapping("/archivo/{nombre}")
    public ResponseEntity<Resource> getImagenPorNombre(@PathVariable String nombre) {
        try {
            Path ruta = Paths.get("/app/images/capturas/").resolve(nombre).normalize();
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Elimina una imagen en función de su ID.
     *
     * @param idImagen identificador de la imagen a eliminar
     * @return ResponseEntity con mensaje de éxito o status HTTP según el resultado
     */
    @DeleteMapping("/{idImagen}")
    public ResponseEntity<String> eliminarImagen(@PathVariable int idImagen) {
        return imagenHistorialService.eliminarImagenPorId(idImagen);
    }
}
