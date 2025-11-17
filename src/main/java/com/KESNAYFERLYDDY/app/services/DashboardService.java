package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardService {
    private final ObjectMapper mapper = new ObjectMapper();

    public int cantidadDeClientes() throws Exception {
        String url = ApiConfig.HOST + "/clientes/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int cantidadDeVentas() throws Exception {
        String url = ApiConfig.HOST + "/ventas/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int cantidadDeProductos() throws Exception {
        String url = ApiConfig.HOST + "/muebles/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int cantidadDeEmpleados() throws Exception {
        String url = ApiConfig.HOST + "/empleados/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }
}
