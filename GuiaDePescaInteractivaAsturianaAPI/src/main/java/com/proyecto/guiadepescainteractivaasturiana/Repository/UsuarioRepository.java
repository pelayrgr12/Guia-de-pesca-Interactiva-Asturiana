package com.proyecto.guiadepescainteractivaasturiana.Repository;


import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByNombre(String nombre);


    List<Usuario>findAll();

}
