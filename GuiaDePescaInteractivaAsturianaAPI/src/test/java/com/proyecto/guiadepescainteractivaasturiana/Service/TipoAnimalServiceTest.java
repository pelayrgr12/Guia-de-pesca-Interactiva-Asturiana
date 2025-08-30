package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.TipoAnimal;
import com.proyecto.guiadepescainteractivaasturiana.Repository.TipoAnimalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TipoAnimalServiceTest {

    @Autowired
    private TipoAnimalService tipoAnimalService;

    @MockBean
    private TipoAnimalRepository tipoAnimalRepository;

    @Test
    void findAll_deberiaRetornarListaDeTipos() {

        TipoAnimal pez = new TipoAnimal();
        pez.setIdTipo(1);
        pez.setNombre("Pez");

        TipoAnimal molusco = new TipoAnimal();
        molusco.setIdTipo(2);
        molusco.setNombre("Molusco");

        when(tipoAnimalRepository.findAll()).thenReturn(List.of(pez, molusco));


        List<TipoAnimal> resultado = tipoAnimalService.findAll();
        assertEquals(2, resultado.size());
        assertEquals("Pez", resultado.get(0).getNombre());
        verify(tipoAnimalRepository).findAll();
    }
}
