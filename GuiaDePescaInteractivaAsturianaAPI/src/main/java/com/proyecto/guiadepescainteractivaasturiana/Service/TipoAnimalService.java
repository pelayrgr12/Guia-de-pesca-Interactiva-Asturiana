package com.proyecto.guiadepescainteractivaasturiana.Service;

import com.proyecto.guiadepescainteractivaasturiana.Entities.TipoAnimal;
import com.proyecto.guiadepescainteractivaasturiana.Repository.TipoAnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoAnimalService {

    @Autowired
    private TipoAnimalRepository tipoAnimalRepository;

    public List<TipoAnimal> findAll(){
        return  tipoAnimalRepository.findAll();
    }
}
