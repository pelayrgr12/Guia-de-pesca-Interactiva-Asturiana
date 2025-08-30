package com.proyecto.guiadepescainteractivaasturiana.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controlador REST para consultar el clima mediante la API de OpenWeather.
 * <p>
 * Utiliza {@link RestTemplate} para realizar peticiones HTTP a
 * OpenWeather y retorna la respuesta en formato JSON crudo.
 * </p>
 * <p>
 * No cambia la lógica actual, solo adiciona JavaDoc para documentación.
 * </p>
 *
 * @author Pelayo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/clima")
public class OpenWeatherController {

    @Value("${openweather.apikey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtiene la información meteorológica de una ciudad en España.
     * <p>
     * Realiza una petición GET a la API de OpenWeather con unidades métricas
     * y lenguaje español. Devuelve el JSON puro obtenido.
     * </p>
     *
     * @param ciudad nombre de la ciudad a consultar (sin codificación URL)
     * @return {@code ResponseEntity<String>} con el contenido JSON si es exitoso,
     *         o un mensaje de error con estado {@link HttpStatus#INTERNAL_SERVER_ERROR}
     *         en caso de fallo.
     */
    @GetMapping
    public ResponseEntity<?> obtenerClima(@RequestParam String ciudad) {
        try {
            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s,ES&units=metric&lang=es&appid=%s",
                    URLEncoder.encode(ciudad, StandardCharsets.UTF_8), apiKey
            );
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar el clima: " + e.getMessage());
        }
    }
}
