package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.EmpleadosDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class EmpleadoService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<EmpleadosDto> listarEmpleados() throws Exception {
        String url = ApiConfig.HOST + "/empleados/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<EmpleadosDto>>() {});
        } else {
            throw new RuntimeException("Error listar empleados: " + r.statusCode());
        }
    }

    public void insertarEmpleado(EmpleadosDto empleado) throws Exception {
        String url = ApiConfig.HOST + "/empleados/";
        String empleadoJson = mapper.writeValueAsString(empleado);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(empleadoJson))
            .build();
        HttpClient emple = HttpClient.newHttpClient();
        HttpResponse<String> response = emple.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            System.out.println("Empleado insertado correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al insertar empleado: " + response.statusCode() + " - " + response.body());
        }
    }

    public void editarEmpleado(EmpleadosDto empleado) throws Exception {
        String url = ApiConfig.HOST + "/empleados/" + empleado.getIdEmpleado();
        String empleadoJson = mapper.writeValueAsString(empleado);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(empleadoJson))
            .build();

        HttpClient emple = HttpClient.newHttpClient();
        HttpResponse<String> response = emple.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Empleado actualizado correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al actualizar empleado: " + response.statusCode() + " - " + response.body());
        }
    }

    public void eliminarEmpleado(Integer empleadoID) throws Exception {
        String url = ApiConfig.HOST + "/empleados/" + empleadoID;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();

        HttpClient emple = HttpClient.newHttpClient();
        HttpResponse<String> response = emple.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Empleado eliminado correctamente. ID: " + empleadoID);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("No se encontró el empleado con ID " + empleadoID);
        } else {
            throw new RuntimeException("Error al eliminar empleado: " 
                + response.statusCode() + " - " + response.body());
        }
    }

    public static Connection getConexion() throws Exception {
        String url = "jdbc:sqlserver://LOCALHOST:1433;databaseName=Muebleria;encrypt=true;trustServerCertificate=true";
        String usuario = "sqluser";
        String password = "nayeliadmin123456";

        Connection conexion = DriverManager.getConnection(url, usuario, password);
        return conexion;
    }
}
