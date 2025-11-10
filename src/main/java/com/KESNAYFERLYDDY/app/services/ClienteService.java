package com.KESNAYFERLYDDY.app.services;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.ClienteDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClienteService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ClienteDto> listarClientes() throws Exception {
        String url = ApiConfig.HOST + "/clientes/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<ClienteDto>>() {});
        } else {
            throw new RuntimeException("Error listar clientes: " + r.statusCode());
        }
    }

    public void insertarCliente(ClienteDto cliente) throws Exception {
        String url = ApiConfig.HOST + "/clientes/registrar";
        String clienteJson = mapper.writeValueAsString(cliente);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(clienteJson))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            System.out.println("Cliente insertado correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al insertar cliente: " + response.statusCode() + " - " + response.body());
        }
    }

    public void editarCliente(ClienteDto cliente) throws Exception {
    String url = ApiConfig.HOST + "/clientes/" + cliente.getIdCliente();
    String clienteJson = mapper.writeValueAsString(cliente);
    
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(clienteJson)) 
        .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        System.out.println("Cliente actualizado correctamente: " + response.body());
    } else {
        throw new RuntimeException("Error al actualizar cliente: " + response.statusCode() + " - " + response.body());
    }
}


    public void eliminarCliente(Integer clienteID) throws Exception {
        String url = ApiConfig.HOST + "/clientes/" + clienteID;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Cliente eliminado correctamente. ID: " + clienteID);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("No se encontró el cliente con ID " + clienteID);
        } else {
        throw new RuntimeException("Error al eliminar cliente: " 
            + response.statusCode() + " - " + response.body());
    }
}


}
