package com.proyecto.guiadepescainteractivaasturiana.Repository;

import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPuntoRepository  extends JpaRepository<HistorialPunto, Integer> {


    List<HistorialPunto> findByPuntoIdPunto(int idPunto);
}
