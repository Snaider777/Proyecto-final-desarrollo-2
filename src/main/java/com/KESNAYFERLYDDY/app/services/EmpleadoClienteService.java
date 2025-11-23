package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.EmpleadosDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmpleadoClienteService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<EmpleadosDto> listarEmpleados() throws Exception {
        String base = ApiConfig.HOST.endsWith("/") ? ApiConfig.HOST.substring(0, ApiConfig.HOST.length()-1) : ApiConfig.HOST;
        String url = base + "/empleados/";

        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<EmpleadosDto>>() {});
        }
        throw new RuntimeException("Error listar empleados: " + r.statusCode() + " - " + r.body());
    }
}
