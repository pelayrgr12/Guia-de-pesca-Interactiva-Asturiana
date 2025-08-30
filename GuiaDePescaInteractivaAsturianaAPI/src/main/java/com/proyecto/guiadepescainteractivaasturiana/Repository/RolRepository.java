package com.proyecto.guiadepescainteractivaasturiana.Repository;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByRol(String rol);
}
