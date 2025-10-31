package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.ClienteDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClienteService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ClienteDto> listarClientes() throws Exception {
        String url = ApiConfig.BASE_URL + "/clientes/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<ClienteDto>>() {});
        } else {
            throw new RuntimeException("Error listar clientes: " + r.statusCode());
        }
    }
}
