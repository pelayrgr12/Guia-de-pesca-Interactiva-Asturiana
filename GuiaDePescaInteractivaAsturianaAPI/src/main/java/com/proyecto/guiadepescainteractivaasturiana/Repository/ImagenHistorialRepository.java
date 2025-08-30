package com.proyecto.guiadepescainteractivaasturiana.Repository;

import com.proyecto.guiadepescainteractivaasturiana.Entities.ImagenHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenHistorialRepository  extends JpaRepository<ImagenHistorial, Integer> {
    List<ImagenHistorial> findByHistorial_IdHistorial(Integer idHistorial);


}
