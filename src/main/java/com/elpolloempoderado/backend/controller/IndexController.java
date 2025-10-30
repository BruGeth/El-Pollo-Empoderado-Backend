package com.elpolloempoderado.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IndexController {

    @GetMapping("/index")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API de Pollería El Empoderado funcionando correctamente");
        response.put("status", "OK");
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("version", "0.1");
        response.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("service", "El Pollo Empoderado Backend");
        return response;
    }
}

