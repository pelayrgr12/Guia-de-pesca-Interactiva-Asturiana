package com.proyecto.guiadepescainteractivaasturiana.Repository;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface PuntoRepository extends JpaRepository<Punto, Integer> {
    List<Punto> findAllByUsuarioIdUsuario(int idUsuario);
    Optional<Punto> findAllByIdPunto(int id);
    Optional<Punto> findByLatitudAndLongitud(Double latitud, Double longitud);
    Optional<Punto>findByNombre(String nombre);


}

