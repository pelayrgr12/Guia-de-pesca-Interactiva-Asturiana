package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.DTo.MedidaDTO;
import com.proyecto.guiadepescainteractivaasturiana.Entities.Medida;
import com.proyecto.guiadepescainteractivaasturiana.Entities.TipoAnimal;
import com.proyecto.guiadepescainteractivaasturiana.Repository.MedidaRepository;
import com.proyecto.guiadepescainteractivaasturiana.Repository.TipoAnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class MedidaService {

    @Autowired
    private MedidaRepository medidaRepository;
    @Autowired
    private TipoAnimalRepository tipoAnimalRepository;

    private  String rutamedidas = "/app/images/medidas/";


    public List<Medida> obtenerMedidasPorTipo(int idTipo) {
        return medidaRepository.findByTipoAnimalIdTipo(idTipo);
    }
    public Medida obtenerPorId(int id) {
        return medidaRepository.findById(id).orElse(null);
    }

    public void eliminarMedida(int id) {
        medidaRepository.deleteById(id);
    }
    public List<Medida> obtenerTodas() {
        return medidaRepository.findAll();
    }

    public MedidaDTO guardarMedida(MedidaDTO medidaDTO) {
        Medida medida = new Medida();
        medida.setNombreComun(medidaDTO.getNombreComun());
        medida.setNombreCientifico(medidaDTO.getNombreCientifico());
        medida.setTallaMinima(medidaDTO.getTallaMinima());
        medida.setImagen(medidaDTO.getImagen());


        Optional<TipoAnimal> tipoOpt = tipoAnimalRepository.findById(medidaDTO.getIdTipo());
        if (tipoOpt.isEmpty()) {
            throw new IllegalArgumentException("TipoAnimal con id " + medidaDTO.getIdTipo() + " no encontrado");
        }

        medida.setTipoAnimal(tipoOpt.get());

        medidaRepository.save(medida);
        return medidaDTO;
    }



    public ResponseEntity<String> subirImagenMedida(MultipartFile imagen) {
        try {
            if (imagen == null || imagen.isEmpty()) {
                return ResponseEntity.badRequest().body("[ERROR] No se proporcionó ninguna imagen.");
            }

            String nombreArchivo = imagen.getOriginalFilename();
            if (nombreArchivo == null || nombreArchivo.isBlank()) {
                return ResponseEntity.badRequest().body("[ERROR] Nombre de archivo inválido.");
            }

            Path rutaDestino = Paths.get(rutamedidas, nombreArchivo);


            Files.createDirectories(rutaDestino.getParent());

            // DEBUG
            System.out.println("[DEBUG] Nombre archivo: " + nombreArchivo);
            System.out.println("[DEBUG] Ruta final destino: " + rutaDestino.toAbsolutePath());

            // Copiar archivo
            Files.copy(imagen.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Imagen de medida guardada correctamente en: " + rutaDestino.toAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar imagen: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }


    public boolean actualizarMedida(int id, Medida medidaNueva) {
        Optional<Medida> medidaExistente = medidaRepository.findById(id);
        if (medidaExistente.isEmpty()) return false;

        Medida existente = medidaExistente.get();

        if (medidaNueva.getNombreComun() != null && !medidaNueva.getNombreComun().isBlank()) {
            existente.setNombreComun(medidaNueva.getNombreComun());
        }

        if (medidaNueva.getNombreCientifico() != null && !medidaNueva.getNombreCientifico().isBlank()) {
            existente.setNombreCientifico(medidaNueva.getNombreCientifico());
        }

        if (medidaNueva.getTallaMinima() != null && !medidaNueva.getTallaMinima().isBlank()) {
            existente.setTallaMinima(medidaNueva.getTallaMinima());
        }

        if (medidaNueva.getImagen() != null && !medidaNueva.getImagen().isBlank()) {
            existente.setImagen(medidaNueva.getImagen());
        }

        if (medidaNueva.getTipoAnimal() != null && medidaNueva.getTipoAnimal().getIdTipo() != existente.getTipoAnimal().getIdTipo()) {
            existente.setTipoAnimal(medidaNueva.getTipoAnimal());
        }

        medidaRepository.save(existente);
        return true;
    }

    public void setRutaMedidas(String ruta) { // ➕ Añade esto para el test
        this.rutamedidas = ruta;
    }


}
