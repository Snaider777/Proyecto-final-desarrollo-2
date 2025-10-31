package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.UsuarioDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthService {
    private final ObjectMapper mapper = new ObjectMapper();

    public boolean login(UsuarioDto u) throws Exception {
        String json = mapper.writeValueAsString(u);
        HttpRequest req = ApiClient.jsonRequest(ApiConfig.BASE_URL + "/auth/IniciarSesion")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            String body = resp.body().trim();
            // la API devuelve booleano 'true' o 'false'
            return "true".equalsIgnoreCase(body) || body.contains("true");
        } else {
            throw new RuntimeException("Error en login: " + resp.statusCode());
        }
    }
}
