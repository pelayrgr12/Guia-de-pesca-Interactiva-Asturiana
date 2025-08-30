package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.HistorialPuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import com.proyecto.guiadepescainteractivaasturiana.Repository.HistorialPuntoRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.PuntoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HistorialPuntoServiceTest {

    @Autowired
    private HistorialPuntoService historialPuntoService;

    @MockBean
    private HistorialPuntoRepository historialPuntoRepository;

    @MockBean
    private PuntoRepository puntoRepository;

    @Test
    void guardarHistorial_RetornarDTO() {
        // Arrange
        HistorialPuntoDTO dto = new HistorialPuntoDTO();
        dto.setIdPunto(1);
        LocalDate fechaLocal = LocalDate.of(2024, 5, 20);
        dto.setFecha(Timestamp.valueOf(fechaLocal.atStartOfDay()));
        dto.setDescripcion("Lubina capturada");

        Punto puntoSimulado = new Punto();
        puntoSimulado.setIdPunto(1);

        when(puntoRepository.findById(1)).thenReturn(Optional.of(puntoSimulado));

        HistorialPunto historialGuardado = new HistorialPunto();
        historialGuardado.setIdHistorial(10);
        historialGuardado.setPunto(puntoSimulado);
        historialGuardado.setFecha(dto.getFecha());
        historialGuardado.setDescripcion(dto.getDescripcion());

        when(historialPuntoRepository.save(any(HistorialPunto.class))).thenReturn(historialGuardado);

        HistorialPuntoDTO resultado = historialPuntoService.guardarHistorial(dto);


        assertNotNull(resultado);
        assertEquals(10, resultado.getIdHistorial());
        assertEquals(1, resultado.getIdPunto());
        assertEquals("Lubina capturada", resultado.getDescripcion());
        assertEquals(LocalDate.of(2024, 5, 20), resultado.getFecha().toLocalDateTime().toLocalDate());

        verify(historialPuntoRepository).save(any(HistorialPunto.class));
    }

    @Test
    void guardarHistorial_devuelveNull_siElPuntoNoExiste() {
        // Arrange
        HistorialPuntoDTO dto = new HistorialPuntoDTO();
        dto.setIdPunto(999);

        when(puntoRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        HistorialPuntoDTO resultado = historialPuntoService.guardarHistorial(dto);

        // Assert
        assertNull(resultado);
    }
}
