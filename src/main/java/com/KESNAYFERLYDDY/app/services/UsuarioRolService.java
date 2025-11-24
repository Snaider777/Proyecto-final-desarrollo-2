package com.KESNAYFERLYDDY.app.services;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UsuarioRolService {
    private final ObjectMapper mapper = new ObjectMapper();

    public void asignarUsuarioRol(Integer idUsuario, Integer idRol) throws Exception {
        String url = ApiConfig.HOST + "/usuario_roles/";
        Asignacion a = new Asignacion(); a.idUsuario = idUsuario; a.idRol = idRol;
        String json = mapper.writeValueAsString(a);
        HttpRequest req = ApiClient.jsonRequest(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 201) throw new RuntimeException("Error asignar rol al usuario");
    }
    static class Asignacion { public Integer idUsuario; public Integer idRol; }
}
