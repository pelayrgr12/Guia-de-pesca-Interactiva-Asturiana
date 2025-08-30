package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.Rol;
import com.proyecto.guiadepescainteractivaasturiana.Repository.RolRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RolRepository rolRepository;

    public RoleService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }
    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }
    public Rol obtenerRolPorNombre(String rol) {
        return rolRepository.findByRol(rol)
                .orElseThrow(() -> new RuntimeException("Error: Rol " + rol + " no encontrado"));
    }



}