package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.models.UsuarioDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
public class UsuarioService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<UsuarioDto> listarUsuarios() throws Exception {
        String url = ApiConfig.HOST + "/usuarios/"; // espera GET /usuarios/
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<UsuarioDto>>() {});
        }
        throw new RuntimeException("Error listar usuarios: " + r.statusCode() + " - " + r.body());
    }

    public UsuarioDto crearUsuario(UsuarioDto u) throws Exception {
        String url = ApiConfig.HOST + "/auth/registrar"; 
        String json = mapper.writeValueAsString(u);
        HttpRequest req = ApiClient.jsonRequest(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200 || r.statusCode() == 201) {
            return mapper.readValue(r.body(), UsuarioDto.class);
        }
        throw new RuntimeException("Error crear usuario: " + r.statusCode() + " - " + r.body());
    }

    public UsuarioDto editarUsuario(Integer id, UsuarioDto u) throws Exception {
        String url = ApiConfig.HOST + "/usuarios/" + id; // espera PUT /usuarios/{id}
        String json = mapper.writeValueAsString(u);
        HttpRequest req = ApiClient.jsonRequest(url).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), UsuarioDto.class);
        }
        throw new RuntimeException("Error editar usuario: " + r.statusCode() + " - " + r.body());
    }

    public void eliminarUsuario(Integer id) throws Exception {
        String url = ApiConfig.HOST + "/usuarios/" + id; // espera DELETE /usuarios/{id}
        HttpRequest req = ApiClient.jsonRequest(url).DELETE().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 204) {
            throw new RuntimeException("Error eliminar usuario: " + r.statusCode() + " - " + r.body());
        }
    }

    // asignar usuario a empleado (usa la tabla Usuario_Empleados)
    public void asignarUsuarioAEmpleado(Integer idUsuario, Integer idEmpleado) throws Exception {
        String url = ApiConfig.HOST + "/usuario_empleados/"; // espera POST
        AsignacionBody body = new AsignacionBody(); body.idUsuario = idUsuario; body.idEmpleado = idEmpleado;
        String json = mapper.writeValueAsString(body);
        HttpRequest req = ApiClient.jsonRequest(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 201) {
            throw new RuntimeException("Error asignar usuario a empleado: " + r.statusCode() + " - " + r.body());
        }
    }
    public UsuarioDto obtenerUsuarioPorEmpleado(Integer idEmpleado) {
        try {
            String url = ApiConfig.HOST + "/usuario_empleados/empleado/" + idEmpleado;
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> response = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Convertimos el JSON recibido a UsuarioDto
                return new ObjectMapper().readValue(response.body(), UsuarioDto.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retorna null si no tiene usuario o hubo error
    }
    static class AsignacionBody { public Integer idUsuario; public Integer idEmpleado; }
}
