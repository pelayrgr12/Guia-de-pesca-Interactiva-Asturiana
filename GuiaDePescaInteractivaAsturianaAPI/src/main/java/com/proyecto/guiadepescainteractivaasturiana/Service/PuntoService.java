package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.PuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Usuario;
import com.proyecto.guiadepescainteractivaasturiana.Repository.PuntoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuntoService {

    @Autowired
    private PuntoRepository puntoRepository;


    public List<Punto> findAllByIdUsuario(int idUsuario){
      return   puntoRepository.findAllByUsuarioIdUsuario(idUsuario);
    }

    public Optional<Punto>findByIdPunto(int id){
        return puntoRepository.findAllByIdPunto(id);
    }

    public boolean deletePunto(PuntoDTO puntoDTO, Usuario usuario) {
        Optional<Punto> puntoOpt = puntoRepository.findByLatitudAndLongitud(puntoDTO.getLatitud(), puntoDTO.getLongitud());

        if (puntoOpt.isPresent()) {
            Punto punto = puntoOpt.get();
            if (punto.getUsuario().getIdUsuario() == usuario.getIdUsuario()) {
                puntoRepository.delete(punto);
                return true;
            }
        }
        return false;
    }

    public boolean modificarPunto(Punto puntoActualizado, Usuario usuario) {
        Optional<Punto> puntoOpt = puntoRepository.findAllByIdPunto(puntoActualizado.getIdPunto());

        if (puntoOpt.isEmpty()) return false;

        Punto puntoExistente = puntoOpt.get();
        List<Punto> puntosUsuario = puntoRepository.findAllByUsuarioIdUsuario(usuario.getIdUsuario());
        for (Punto p : puntosUsuario) {
            if (p.getIdPunto() != puntoActualizado.getIdPunto() &&
                    p.getNombre().equalsIgnoreCase(puntoActualizado.getNombre())) {
                throw new IllegalArgumentException("Ya tienes otro punto con ese nombre.");
            }
        }

        if (puntoExistente.getUsuario().getIdUsuario() != usuario.getIdUsuario()) return false;
        puntoExistente.setNombre(puntoActualizado.getNombre());
        puntoExistente.setDescripcion(puntoActualizado.getDescripcion());
        puntoRepository.save(puntoExistente);
        return true;
    }


    public Punto guardarPunto(PuntoDTO puntoDTO, Usuario usuario) {
        List<Punto> puntosUsuario = puntoRepository.findAllByUsuarioIdUsuario(usuario.getIdUsuario());
        for (Punto existente : puntosUsuario) {
            if (existente.getNombre().equalsIgnoreCase(puntoDTO.getNombre())) {
                throw new IllegalArgumentException("Ya existe un punto con ese nombre");
            }
        }

        Punto punto = new Punto();
        punto.setLatitud(puntoDTO.getLatitud());
        punto.setLongitud(puntoDTO.getLongitud());
        punto.setNombre(puntoDTO.getNombre());
        punto.setDescripcion(puntoDTO.getDescripcion());
        punto.setUsuario(usuario);

        return puntoRepository.save(punto);
    }




}
