package com.KESNAYFERLYDDY.app.services;
import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.KESNAYFERLYDDY.app.models.VentaDto;
import com.KESNAYFERLYDDY.app.models.DetalleVentasDto;
import com.KESNAYFERLYDDY.app.models.MuebleDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class VentaService {
    private final ObjectMapper mapper;
    
    public VentaService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public List<VentaDto> listarVentas() throws Exception {
        String url = ApiConfig.HOST + "/ventas/";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<VentaDto>>() {});
        } else {
            throw new RuntimeException("Error listar Ventas: " + r.statusCode());
        }
    }
    
    public VentaDto obtenerVentaPorId(Integer idVenta) throws Exception {
        String url = ApiConfig.HOST + "/ventas/" + idVenta;
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        
        VentaDto venta = null;
        if (r.statusCode() == 200) {
            venta = mapper.readValue(r.body(), VentaDto.class);
        } else {
            throw new RuntimeException("Error obtener Venta: " + r.statusCode());
        }
        String urlDetalles = ApiConfig.HOST + "/ventas/" + idVenta + "/detalles";
        try {
            HttpRequest reqDetalles = ApiClient.jsonRequest(urlDetalles).GET().build();
            HttpResponse<String> rDetalles = ApiClient.get().send(reqDetalles, HttpResponse.BodyHandlers.ofString());
            
            if (rDetalles.statusCode() == 200) {
                List<DetalleVentasDto> detalles = mapper.readValue(rDetalles.body(), new TypeReference<List<DetalleVentasDto>>() {});
                venta.setDetalles(detalles);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener detalles: " + e.getMessage());
        }
        return venta;
    }
    
    public List<DetalleVentasDto> obtenerDetallesPorVentaId(Integer idVenta) throws Exception {
        String urlDetalles = ApiConfig.HOST + "/ventas/" + idVenta + "/detalles";
        HttpRequest reqDetalles = ApiClient.jsonRequest(urlDetalles).GET().build();
        HttpResponse<String> rDetalles = ApiClient.get().send(reqDetalles, HttpResponse.BodyHandlers.ofString());
        
        if (rDetalles.statusCode() == 200) {
            return mapper.readValue(rDetalles.body(), new TypeReference<List<DetalleVentasDto>>() {});
        } else {
            throw new RuntimeException("Error al obtener detalles: " + rDetalles.statusCode());
        }
    }
    
    public void insertarVenta(VentaDto venta) throws Exception {
        String url = ApiConfig.HOST + "/ventas/";
        String VentaJson = mapper.writeValueAsString(venta);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(VentaJson))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            System.out.println("Venta insertada correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al insertar venta: " + response.statusCode() + " - " + response.body());
        }
    }

    public void editarVenta(VentaDto venta) throws Exception {
        String url = ApiConfig.HOST + "/ventas/" + venta.getIdVenta();
        String VentaJson = mapper.writeValueAsString(venta);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(VentaJson)) 
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Venta actualizada correctamente: " + response.body());
        } else {
            throw new RuntimeException("Error al actualizar venta: " + response.statusCode() + " - " + response.body());
        }
    }

    public void eliminarVenta(Integer ventaID) throws Exception {
        String url = ApiConfig.HOST + "/ventas/" + ventaID;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Venta eliminada correctamente. ID: " + ventaID);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("No se encontró el Venta con ID " + ventaID);
        } else {
        throw new RuntimeException("Error al eliminar Venta: " 
            + response.statusCode() + " - " + response.body());
        }
    }

    public List<VentaDto> listarVentasFechas(Date fechaInicio, Date fechaFin) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String inicioStr = fechaInicio.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                                    .format(formatter);
                                    
        String finStr = fechaFin.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                                .format(formatter);
    
        String url = String.format("%s/ventas/rango?inicio=%s&fin=%s", ApiConfig.HOST, inicioStr, finStr);
        
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<VentaDto>>() {});
        } 
        else {
            throw new RuntimeException("Error al listar Ventas por rango (" + r.statusCode() + "): " + r.body());
        }
    }

    public List<VentaDto> listarVentasMesActual() throws Exception {
        String url = ApiConfig.HOST + "/ventas/mes-actual";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<VentaDto>>() {});
        } else {
            throw new RuntimeException("Error al listar Ventas del mes actual: " + r.statusCode());
        }
    }

    public List<com.KESNAYFERLYDDY.app.models.MuebleVendidoDto> obtenerMueblesMasVendidosDelMes() throws Exception {
        String url = ApiConfig.HOST + "/ventas/muebles-mas-vendidos-mes";
        HttpRequest req = ApiClient.jsonRequest(url).GET().build();
        HttpResponse<String> r = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() == 200) {
            return mapper.readValue(r.body(), new TypeReference<List<com.KESNAYFERLYDDY.app.models.MuebleVendidoDto>>() {});
        } else {
            throw new RuntimeException("Error al obtener muebles más vendidos: " + r.statusCode());
        }
    }
}
