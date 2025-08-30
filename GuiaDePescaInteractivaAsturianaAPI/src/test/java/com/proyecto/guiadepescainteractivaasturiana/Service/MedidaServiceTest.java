package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.MedidaDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Medida;
import com.proyecto.guiadepescainteractivaasturiana.Entities.TipoAnimal;
import com.proyecto.guiadepescainteractivaasturiana.Repository.MedidaRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.TipoAnimalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MedidaServiceTest {

    @Autowired
    private MedidaService medidaService;

    @MockBean
    private MedidaRepository medidaRepository;

    @MockBean
    private TipoAnimalRepository tipoAnimalRepository;

    @Test
    void guardarMedida_deberiaGuardarYRetornarDTO() {
        // Arrange
        MedidaDTO dto = new MedidaDTO();
        dto.setNombreComun("Lubina");
        dto.setNombreCientifico("Dicentrarchus labrax");
        dto.setTallaMinima("42 cm");
        dto.setImagen("lubina.jpg");
        dto.setIdTipo(1);

        TipoAnimal tipo = new TipoAnimal();
        tipo.setIdTipo(1);
        when(tipoAnimalRepository.findById(1)).thenReturn(Optional.of(tipo));

        // Act
        MedidaDTO resultado = medidaService.guardarMedida(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Lubina", resultado.getNombreComun());
        verify(medidaRepository).save(any(Medida.class));
    }

    @Test
    void guardarMedida_lanzaExcepcion_siTipoNoExiste() {
        // Arrange
        MedidaDTO dto = new MedidaDTO();
        dto.setIdTipo(999);
        when(tipoAnimalRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> medidaService.guardarMedida(dto));
    }

    @Test
    void subirImagenMedida_deberiaGuardarImagen() throws IOException {

        Path tempDir = Files.createTempDirectory("medidas-test");
        medidaService.setRutaMedidas(tempDir.toString()); // ⬅️ Redefinir ruta destino

        MultipartFile imagen = new MockMultipartFile(
                "imagen",
                "medida-test.jpg",
                "image/jpeg",
                "contenido-ficticio".getBytes()
        );


        ResponseEntity<String> respuesta = medidaService.subirImagenMedida(imagen);


        assertEquals(200, respuesta.getStatusCodeValue());
        assertTrue(respuesta.getBody().contains("guardada correctamente"));


        assertTrue(Files.exists(tempDir.resolve("medida-test.jpg")));
    }


    @Test
    void actualizarMedida_deberiaActualizarCampos() {
        // Arrange
        Medida existente = new Medida();
        existente.setIdMedida(1);
        existente.setNombreComun("Vieja");
        existente.setTallaMinima("20 cm");

        Medida modificada = new Medida();
        modificada.setNombreComun("Pulpo");
        modificada.setTallaMinima("30 cm");

        when(medidaRepository.findById(1)).thenReturn(Optional.of(existente));

        // Act
        boolean actualizado = medidaService.actualizarMedida(1, modificada);

        // Assert
        assertTrue(actualizado);
        assertEquals("Pulpo", existente.getNombreComun());
        verify(medidaRepository).save(existente);
    }

    @Test
    void actualizarMedida_devuelveFalse_siNoExiste() {

        when(medidaRepository.findById(123)).thenReturn(Optional.empty());

        boolean actualizado = medidaService.actualizarMedida(123, new Medida());
        assertFalse(actualizado);
    }
}
