package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.PuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Repository.PuntoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PuntoServiceTest {

    @Autowired
    private PuntoService puntoService;

    @MockBean
    private PuntoRepository puntoRepository;

    @Test
    void guardarPunto_deberiaGuardarSiNoExisteNombreDuplicado() {
        // Arrange
        PuntoDTO dto = new PuntoDTO();
        dto.setNombre("Zona Norte");
        dto.setLatitud(43.5);
        dto.setLongitud(-5.6);
        dto.setDescripcion("Zona de pesca activa");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        when(puntoRepository.findAllByUsuarioIdUsuario(1)).thenReturn(List.of());

        Punto puntoGuardado = new Punto();
        puntoGuardado.setIdPunto(10);
        puntoGuardado.setNombre("Zona Norte");
        when(puntoRepository.save(any(Punto.class))).thenReturn(puntoGuardado);


        Punto resultado = puntoService.guardarPunto(dto, usuario);
        assertNotNull(resultado);
        assertEquals("Zona Norte", resultado.getNombre());
        verify(puntoRepository).save(any(Punto.class));
    }

    @Test
    void guardarPunto_lanzaExcepcion_siNombreDuplicado() {
        // Arrange
        PuntoDTO dto = new PuntoDTO();
        dto.setNombre("Playa X");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Punto existente = new Punto();
        existente.setNombre("Playa X");

        when(puntoRepository.findAllByUsuarioIdUsuario(1)).thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class, () -> puntoService.guardarPunto(dto, usuario));
    }

    @Test
    void modificarPunto_deberiaActualizarSiEsValido() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Punto existente = new Punto();
        existente.setIdPunto(1);
        existente.setNombre("Antiguo");
        existente.setUsuario(usuario);

        Punto modificado = new Punto();
        modificado.setIdPunto(1);
        modificado.setNombre("Nuevo Nombre");
        modificado.setDescripcion("Actualizado");

        when(puntoRepository.findAllByIdPunto(1)).thenReturn(Optional.of(existente));
        when(puntoRepository.findAllByUsuarioIdUsuario(1)).thenReturn(List.of(existente));

        // Act
        boolean resultado = puntoService.modificarPunto(modificado, usuario);

        // Assert
        assertTrue(resultado);
        assertEquals("Nuevo Nombre", existente.getNombre());
        verify(puntoRepository).save(existente);
    }

    @Test
    void deletePunto_deberiaEliminarSiPerteneceAlUsuario() {
        // Arrange
        PuntoDTO dto = new PuntoDTO();
        dto.setLatitud(43.2);
        dto.setLongitud(-5.2);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Punto punto = new Punto();
        punto.setUsuario(usuario);

        when(puntoRepository.findByLatitudAndLongitud(dto.getLatitud(), dto.getLongitud()))
                .thenReturn(Optional.of(punto));

        // Act
        boolean eliminado = puntoService.deletePunto(dto, usuario);

        // Assert
        assertTrue(eliminado);
        verify(puntoRepository).delete(punto);
    }

    @Test
    void findByIdPunto_deberiaDevolverOptional() {
        Punto punto = new Punto();
        punto.setIdPunto(1);
        when(puntoRepository.findAllByIdPunto(1)).thenReturn(Optional.of(punto));

        Optional<Punto> resultado = puntoService.findByIdPunto(1);
        assertTrue(resultado.isPresent());
    }

    @Test
    void findAllByIdUsuario_deberiaRetornarLista() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Punto punto1 = new Punto();
        punto1.setIdPunto(1);
        when(puntoRepository.findAllByUsuarioIdUsuario(1)).thenReturn(List.of(punto1));

        List<Punto> lista = puntoService.findAllByIdUsuario(1);
        assertEquals(1, lista.size());
    }
}
