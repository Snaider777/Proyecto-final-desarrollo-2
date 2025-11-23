package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.models.PermisoDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.*;
import java.util.List;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.config.ApiConfig;

public class PermisoService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<PermisoDto> listarPermisos() throws Exception {
        String url = ApiConfig.HOST + "/permisos/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<PermisoDto>>() {});
        }
        throw new RuntimeException("Error listar permisos: " + r.statusCode() + " - " + r.body());
    }

    public PermisoDto crearPermiso(PermisoDto p) throws Exception {
        String url = ApiConfig.HOST + "/permisos/";
        String json = mapper.writeValueAsString(p);
        HttpRequest req = ApiClient.jsonRequest(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200 || r.statusCode() == 201) {
            return mapper.readValue(r.body(), PermisoDto.class);
        }
        throw new RuntimeException("Error crear permiso: " + r.statusCode());
    }

    public void eliminarPermiso(Integer id) throws Exception {
        String url = ApiConfig.HOST + "/permisos/" + id;
        HttpRequest req = ApiClient.jsonRequest(url).DELETE().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 204) throw new RuntimeException("Error eliminar permiso");
    }
    public boolean tienePermiso(String username, String nombrePermiso) {
        try {
            String userSafe = java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8);
            String permSafe = java.net.URLEncoder.encode(nombrePermiso, java.nio.charset.StandardCharsets.UTF_8);

            // GET /permisos/usuario/nombre/{usuario}/permiso/{permiso}
            String url = ApiConfig.HOST + "/permisos/usuario/nombre/" + userSafe + "/permiso/" + permSafe;
            
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            
            if (resp.statusCode() == 200) {
                return Boolean.parseBoolean(resp.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // (error de conexión, etc)
        return false;
    }
}
