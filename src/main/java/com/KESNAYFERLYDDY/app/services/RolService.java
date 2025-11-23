package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.models.RolDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.config.ApiConfig;
public class RolService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<RolDto> listarRoles() throws Exception {
        String url = ApiConfig.HOST + "/roles/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<RolDto>>() {});
        }
        throw new RuntimeException("Error listar roles: " + r.statusCode() + " - " + r.body());
    }

    public RolDto crearRol(RolDto rol) throws Exception {
        String url = ApiConfig.HOST + "/roles/"; // POST /roles/
        String json = mapper.writeValueAsString(rol);
        HttpRequest req = ApiClient.jsonRequest(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200 || r.statusCode() == 201) return mapper.readValue(r.body(), RolDto.class);
        throw new RuntimeException("Error crear rol: " + r.statusCode() + " - " + r.body());
    }

    public void eliminarRol(Integer id) throws Exception {
        String url = ApiConfig.HOST + "/roles/" + id; // DELETE /roles/{id}
        HttpRequest req = ApiClient.jsonRequest(url).DELETE().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 204) throw new RuntimeException("Error eliminar rol: " + r.statusCode());
    }
}
