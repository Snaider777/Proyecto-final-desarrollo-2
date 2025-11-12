package com.KESNAYFERLYDDY.app.services;
import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.MuebleDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MuebleService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<MuebleDto> listarMuebles() throws Exception {
        String url = ApiConfig.HOST + "/muebles/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<MuebleDto>>() {});
        } else {
            throw new RuntimeException("Error listar muebles: " + r.statusCode());
        }
    }
    
    public void insertarMueble(MuebleDto mueble) throws Exception {
        String url = ApiConfig.HOST + "/muebles/";
        String MuebleJson = mapper.writeValueAsString(mueble);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(MuebleJson))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            System.out.println("Mueble insertado correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al insertar mueble: " + response.statusCode() + " - " + response.body());
        }
    }

    public void editarMueble(MuebleDto mueble) throws Exception {
    String url = ApiConfig.HOST + "/muebles/" + mueble.getIdMueble();
    String MuebleJson = mapper.writeValueAsString(mueble);
    System.out.println("DEBUG: nombre = " + mueble.getNombreMueble());
    System.out.println("DEBUG: JSON que se enviara = " + mapper.writeValueAsString(mueble));

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(MuebleJson)) 
        .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        System.out.println("Mueble actualizado correctamente: " + response.body());
    } else {
        throw new RuntimeException("Error al actualizar mueble: " + response.statusCode() + " - " + response.body());
    }
}

    public void eliminarMueble(Integer muebleID) throws Exception {
        String url = ApiConfig.HOST + "/muebles/" + muebleID;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Mueble eliminado correctamente. ID: " + muebleID);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("No se encontró el mueble con ID " + muebleID);
        } else {
        throw new RuntimeException("Error al eliminar mueble: " 
            + response.statusCode() + " - " + response.body());
    }
}
}
