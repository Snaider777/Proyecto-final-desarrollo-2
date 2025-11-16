package com.KESNAYFERLYDDY.app.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.CategoriaDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoriaService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<CategoriaDto> listarCategorias() throws Exception {
        String url = ApiConfig.HOST + "/categorias/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<CategoriaDto>>() {});
        } else {
            throw new RuntimeException("Error listar Categorias: " + r.statusCode());
        }
    }

    public void insertarCategoria(CategoriaDto categoria) throws Exception {
        String url = ApiConfig.HOST + "/categorias/";
        String CategoriaJson = mapper.writeValueAsString(categoria);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(CategoriaJson))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            System.out.println("Categoria insertada correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al insertar categoria: " + response.statusCode() + " - " + response.body());
        }
    }

    public void editarCategoria(CategoriaDto categoria) throws Exception {
    String url = ApiConfig.HOST + "/categorias/" + categoria.getIdCategoria();
    String CategoriaJson = mapper.writeValueAsString(categoria);
    System.out.println("DEBUG: nombre = " + categoria.getCategoria());
    System.out.println("DEBUG: JSON que se enviara = " + mapper.writeValueAsString(categoria));
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(CategoriaJson)) 
        .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        System.out.println("Categoria actualizada correctamente: " + response.body());
    } else {
        throw new RuntimeException("Error al actualizar categoria: " + response.statusCode() + " - " + response.body());
    }
}


    public void eliminarCategoria(Integer categoriaID) throws Exception {
        String url = ApiConfig.HOST + "/categorias/" + categoriaID;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Categoria eliminada correctamente. ID: " + categoriaID);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("No se encontró el Categoria con ID " + categoriaID);
        } else {
        throw new RuntimeException("Error al eliminar Categoria: " 
            + response.statusCode() + " - " + response.body());
    }
}
}
