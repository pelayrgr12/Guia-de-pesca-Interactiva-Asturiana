package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.ImagenHistorial;
import com.proyecto.guiadepescainteractivaasturiana.Repository.HistorialPuntoRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.ImagenHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ImagenHistorialService {

    @Autowired
    private ImagenHistorialRepository imagenHistorialRepository;
    @Autowired
    private  HistorialPuntoRepository historialPuntoRepository;

    private  String rutaCapturas = "/app/images/capturas/";



    public ResponseEntity<String> subirImagen(MultipartFile imagen, Integer idHistorial) {
        try {
            Optional<HistorialPunto> historial = historialPuntoRepository.findById(idHistorial);
            if (historial.isEmpty()) {
                return ResponseEntity.badRequest().body("Historial no encontrado");
            }

            String nombreArchivo = imagen.getOriginalFilename();
            Path rutaDestino = Paths.get(rutaCapturas + "/" + nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            ImagenHistorial nuevaImagen = new ImagenHistorial();
            nuevaImagen.setNombre(nombreArchivo);
            nuevaImagen.setHistorial(historial.get());

            imagenHistorialRepository.save(nuevaImagen);

            return ResponseEntity.ok("Imagen subida correctamente");

        } catch (IOException e) {
            e.printStackTrace(); // Muestra la causa real en consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar imagen: " + e.getClass().getSimpleName() + " - " + e.getMessage());


        }
    }

    public List<String> obtenerNombresPorIdHistorial(Integer idHistorial) {
        return imagenHistorialRepository.findByHistorial_IdHistorial(idHistorial)
                .stream()
                .map(ImagenHistorial::getNombre)
                .toList();
    }

    public ResponseEntity<String> eliminarImagenPorId(int idImagen) {
        Optional<ImagenHistorial> imagenOpt = imagenHistorialRepository.findById(idImagen);
        if (imagenOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImagenHistorial imagen = imagenOpt.get();
        String nombreArchivo = imagen.getNombre();

        Path rutaArchivo = Paths.get(rutaCapturas + "/" + nombreArchivo);

        try {

            if (Files.exists(rutaArchivo)) {
                Files.delete(rutaArchivo);
            }

            imagenHistorialRepository.deleteById(idImagen);

            return ResponseEntity.ok("Imagen eliminada correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    //para test
    public void setRutaCapturas(String ruta) {
        this.rutaCapturas = ruta;
    }




}
