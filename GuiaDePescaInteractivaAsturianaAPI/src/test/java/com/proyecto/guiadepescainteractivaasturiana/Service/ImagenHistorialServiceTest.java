package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.ImagenHistorial;
import com.proyecto.guiadepescainteractivaasturiana.Repository.HistorialPuntoRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.ImagenHistorialRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ImagenHistorialServiceTest {

    @Autowired
    private ImagenHistorialService imagenHistorialService;

    @MockBean
    private ImagenHistorialRepository imagenHistorialRepository;

    @MockBean
    private HistorialPuntoRepository historialPuntoRepository;

    @Test
    void subirImagen_deberiaSubirYGuardar() throws Exception {

        Path tempDir = Files.createTempDirectory("test-capturas");
        imagenHistorialService.setRutaCapturas(tempDir.toString());


        MockMultipartFile mockFile = new MockMultipartFile(
                "archivo", "captura-test.jpg", "image/jpeg", "imagen simulada".getBytes()
        );

        HistorialPunto historial = new HistorialPunto();
        historial.setIdHistorial(1);
        when(historialPuntoRepository.findById(1)).thenReturn(Optional.of(historial));
        ResponseEntity<String> respuesta = imagenHistorialService.subirImagen(mockFile, 1);

        assertEquals(200, respuesta.getStatusCodeValue());
        verify(imagenHistorialRepository, times(1)).save(any(ImagenHistorial.class));
    }

    @Test
    void obtenerNombresPorIdHistorial_deberiaDevolverListaNombres() {
        // Arrange
        ImagenHistorial img1 = new ImagenHistorial();
        img1.setNombre("foto1.jpg");
        ImagenHistorial img2 = new ImagenHistorial();
        img2.setNombre("foto2.jpg");

        when(imagenHistorialRepository.findByHistorial_IdHistorial(1)).thenReturn(List.of(img1, img2));


        List<String> nombres = imagenHistorialService.obtenerNombresPorIdHistorial(1);


        assertEquals(2, nombres.size());
        assertTrue(nombres.contains("foto1.jpg"));
        assertTrue(nombres.contains("foto2.jpg"));
    }

    @Test
    void eliminarImagenPorId_deberiaEliminarImagen() throws IOException {
        // Arrange
        ImagenHistorial imagen = new ImagenHistorial();
        imagen.setId(5);
        imagen.setNombre("imagen-test.jpg");

        when(imagenHistorialRepository.findById(5)).thenReturn(Optional.of(imagen));


        Path path = Path.of("/app/images/capturas/imagen-test.jpg");
        Files.createDirectories(path.getParent()); // asegúrate de que el directorio exista
        Files.write(path, "contenido".getBytes());
        imagenHistorialService.setRutaCapturas("/app/images/capturas");

        ResponseEntity<String> response = imagenHistorialService.eliminarImagenPorId(5);

        assertEquals(200, response.getStatusCodeValue());
        verify(imagenHistorialRepository).deleteById(5);
        assertFalse(Files.exists(path)); // el archivo debería haberse eliminado
    }
}
