package com.proyecto.guiadepescainteractivaasturiana.Controller;

import com.proyecto.guiadepescainteractivaasturiana.DTo.TipoAnimalDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.TipoAnimal;
import com.proyecto.guiadepescainteractivaasturiana.Service.TipoAnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que expone los tipos de animales disponibles en el sistema.
 * <p>
 * Proporciona un endpoint para obtener la lista de tipos de animales
 * en formato DTO. No modifica la lógica existente, solo añade documentación JavaDoc.
 * </p>
 *
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/tipoanimal")
public class TipoAnimalController {

    @Autowired
    private TipoAnimalService tipoAnimalService;

    /**
     * Obtiene todos los tipos de animales registrados.
     *
     * @return ResponseEntity con la lista de TipoAnimalDTO, representando
     *         cada tipo de animal disponible en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<TipoAnimalDTO>> getTipos() {
        List<TipoAnimal> tipoAnimalList = tipoAnimalService.findAll();

        List<TipoAnimalDTO> tipoAnimalDTOS = tipoAnimalList.stream()
                .map(tipo -> new TipoAnimalDTO(tipo.getIdTipo(), tipo.getNombre()))
                .toList();

        return ResponseEntity.ok(tipoAnimalDTOS);
    }
}
