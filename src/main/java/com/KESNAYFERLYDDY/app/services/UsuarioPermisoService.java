package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UsuarioPermisoService {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Asignar permiso a usuario (POST /usuario_permisos/)
     * Body: { "idUsuario": ..., "idPermiso": ... }
     */
    public void asignarUsuarioPermiso(Integer idUsuario, Integer idPermiso) throws Exception {
        String url = ApiConfig.HOST + "/usuario_permisos/";
        Asignacion a = new Asignacion();
        a.idUsuario = idUsuario;
        a.idPermiso = idPermiso;
        String json = mapper.writeValueAsString(a);

        HttpRequest req = ApiClient.jsonRequest(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200 && resp.statusCode() != 201) {
            throw new RuntimeException("Error asignar permiso a usuario: " + resp.statusCode() + " - " + resp.body());
        }
    }

    /**
     * Eliminar asignación usuario-permiso por id (DELETE /usuario_permisos/{id})
     */
    public void eliminarUsuarioPermiso(Integer id) throws Exception {
        String url = ApiConfig.HOST + "/usuario_permisos/" + id;
        HttpRequest req = ApiClient.jsonRequest(url).DELETE().build();
        HttpResponse<String> resp = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200 && resp.statusCode() != 204) {
            throw new RuntimeException("Error eliminar usuario-permiso: " + resp.statusCode());
        }
    }

    static class Asignacion {
        public Integer idUsuario;
        public Integer idPermiso;
    }
}
