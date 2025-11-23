package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
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

    // helper DTO para el body
    static class Asignacion {
        public Integer idRol;
        public Integer idPermiso;
    }
}
