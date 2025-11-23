package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.PermisoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RolPermisoService {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Asigna un permiso a un rol (POST /rol_permisos/)
     * Body esperado: { "idRol": ..., "idPermiso": ... }
     */
    public void asignarRolPermiso(Integer idRol, Integer idPermiso) throws Exception {
        String url = ApiConfig.HOST + "/rol_permisos/";
        Asignacion body = new Asignacion();
        body.idRol = idRol;
        body.idPermiso = idPermiso;
        String json = mapper.writeValueAsString(body);

        HttpRequest req = ApiClient.jsonRequest(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200 && resp.statusCode() != 201) {
            throw new RuntimeException("Error asignar rol-permiso: " + resp.statusCode() + " - " + resp.body());
        }
    }

    /**
     * Elimina una asociación rol-permiso por id (DELETE /rol_permisos/{id})
     */
    public void eliminarRolPermiso(Integer id) throws Exception {
        String url = ApiConfig.HOST + "/rol_permisos/" + id;
        HttpRequest req = ApiClient.jsonRequest(url).DELETE().build();
        HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200 && resp.statusCode() != 204) {
            throw new RuntimeException("Error eliminar rol-permiso: " + resp.statusCode());
        }
    }

    public List<PermisoDto> listarPermisosPorRol(Integer idRol) throws Exception {
        String url = ApiConfig.HOST + "/rol_permisos/rol/" + idRol;
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> res = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        
        if (res.statusCode() == 200) {
            return new ObjectMapper().readValue(res.body(), new TypeReference<List<PermisoDto>>(){});
        }
        return List.of(); // Retorna lista vacía si falla o no hay
    }

    // helper DTO para el body
    static class Asignacion {
        public Integer idRol;
        public Integer idPermiso;
    }
}
