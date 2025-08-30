package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.HistorialPuntoDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.HistorialPunto;
import com.proyecto.guiadepescainteractivaasturiana.Entities.ImagenHistorial;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Punto;
import com.proyecto.guiadepescainteractivaasturiana.Repository.HistorialPuntoRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.PuntoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialPuntoService {

    @Autowired
    private HistorialPuntoRepository historialPuntoRepository;

    @Autowired
    private PuntoRepository puntoRepo;
    @Autowired
    private ImagenHistorialService imagenHistorialService;

    public List<HistorialPunto> findAll(){
       return historialPuntoRepository.findAll();
    }


    public List<HistorialPunto> findByPunto(int idPunto) {
        return historialPuntoRepository.findByPuntoIdPunto(idPunto);
    }

    public HistorialPuntoDTO guardarHistorial(HistorialPuntoDTO dto) {
        Optional<Punto> punto = puntoRepo.findById(dto.getIdPunto());
        if (punto.isEmpty()) {
            return null;
        }

        HistorialPunto historial = new HistorialPunto();
        historial.setPunto(punto.get());
        historial.setFecha(dto.getFecha());
        historial.setDescripcion(dto.getDescripcion());

        historial = historialPuntoRepository.save(historial);


        HistorialPuntoDTO respuesta = new HistorialPuntoDTO();
        respuesta.setIdHistorial(historial.getIdHistorial());
        respuesta.setIdPunto(historial.getPunto().getIdPunto());
        respuesta.setFecha(historial.getFecha());
        respuesta.setDescripcion(historial.getDescripcion());
        respuesta.setImagenes(dto.getImagenes());

        return respuesta;
    }

    public HistorialPuntoDTO actualizarHistorial(HistorialPuntoDTO dto) {
        Optional<HistorialPunto> optionalHistorial = historialPuntoRepository.findById(dto.getIdHistorial());
        Optional<Punto> punto = puntoRepo.findById(dto.getIdPunto());

        if (optionalHistorial.isEmpty() || punto.isEmpty()) {
            return null;
        }

        HistorialPunto historial = optionalHistorial.get();
        historial.setFecha(dto.getFecha());
        historial.setDescripcion(dto.getDescripcion());
        historial.setPunto(punto.get());
        historial = historialPuntoRepository.save(historial);

        HistorialPuntoDTO actualizado = new HistorialPuntoDTO();
        actualizado.setIdHistorial(historial.getIdHistorial());
        actualizado.setIdPunto(historial.getPunto().getIdPunto());
        actualizado.setFecha(historial.getFecha());
        actualizado.setDescripcion(historial.getDescripcion());
        actualizado.setImagenes(dto.getImagenes());

        return actualizado;
    }

    public boolean eliminarHistorial(int id) {
        Optional<HistorialPunto> historialOpt = historialPuntoRepository.findById(id);
        if (historialOpt.isEmpty()) {
            return false;
        }

        HistorialPunto historial = historialOpt.get();

        // Delete associated images
        if (historial.getImagenes() != null) {
            for (ImagenHistorial imagen : historial.getImagenes()) {
                imagenHistorialService.eliminarImagenPorId(imagen.getId());
            }
        }

        // Delete the historial
        historialPuntoRepository.deleteById(id);
        return true;
    }



}
