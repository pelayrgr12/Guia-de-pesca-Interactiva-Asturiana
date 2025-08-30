package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @MockBean
    private RolRepository rolRepository;

    @Test
    void guardarRol_deberiaGuardarCorrectamente() {

        Rol rol = new Rol();
        rol.setRol("ADMIN");
        when(rolRepository.save(rol)).thenReturn(rol);
        Rol resultado = roleService.guardarRol(rol);


        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getRol());
        verify(rolRepository).save(rol);
    }

    @Test
    void obtenerRolPorNombre_deberiaDevolverRolSiExiste() {

        Rol rol = new Rol();
        rol.setRol("USER");
        when(rolRepository.findByRol("USER")).thenReturn(Optional.of(rol));

        Rol resultado = roleService.obtenerRolPorNombre("USER");

        assertNotNull(resultado);
        assertEquals("USER", resultado.getRol());
    }

    @Test
    void obtenerRolPorNombre_lanzaExcepcionSiNoExiste() {
        when(rolRepository.findByRol("INVÁLIDO")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleService.obtenerRolPorNombre("INVÁLIDO"));

        assertEquals("Error: Rol INVÁLIDO no encontrado", ex.getMessage());
    }
}
