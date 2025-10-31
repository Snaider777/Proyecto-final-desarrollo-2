package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DashboardService {
    private final ObjectMapper mapper = new ObjectMapper();

    public int countClientes() throws Exception {
        String url = ApiConfig.BASE_URL + "/clientes/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            List<?> list = mapper.readValue(r.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int countVentas() throws Exception {
        String url = ApiConfig.BASE_URL + "/ventas/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            List<?> list = mapper.readValue(r.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int countProductos() throws Exception {
        String url = ApiConfig.BASE_URL + "/muebles/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            List<?> list = mapper.readValue(r.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }

    public int countUsuarios() throws Exception {
        String url = ApiConfig.BASE_URL + "/usuarios/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            List<?> list = mapper.readValue(r.body(), new TypeReference<List<Object>>() {});
            return list.size();
        } else return 0;
    }
}
