package com.proyecto.guiadepescainteractivaasturiana.Repository;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Medida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedidaRepository extends JpaRepository<Medida, Integer> {

    List<Medida> findByTipoAnimalIdTipo(int idTipo);
}
