package com.KESNAYFERLYDDY.app.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.KESNAYFERLYDDY.app.config.ApiConfig;
import com.KESNAYFERLYDDY.app.http.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.KESNAYFERLYDDY.app.models.MuebleVendidoDto;
import com.KESNAYFERLYDDY.app.models.VentaDto;

public class DashboardService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final VentaService ventaService;

    public DashboardService() {
        this.ventaService = new VentaService();
    }

    public List<VentaDto> ventasDelMes() {
        try {
            return ventaService.listarVentasMesActual();
        } catch (Exception e) {
            System.err.println("Error al obtener ventas del mes: " + e.getMessage());
            return List.of();
        }
    }

    public List<MuebleVendidoDto> mueblesMasVendidosDelMes() {
        try {
            return ventaService.obtenerMueblesMasVendidosDelMes();
        } catch (Exception e) {
            System.err.println("Error al obtener muebles más vendidos: " + e.getMessage());
            return List.of();
        }
    }

    public int cantidadDeClientes() {
        try {
            String url = ApiConfig.HOST + "/clientes/";
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
                return list.size();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener cantidad de clientes: " + e.getMessage());
        }
        return 0;
    }

    public int cantidadDeVentas() {
        try {
            String url = ApiConfig.HOST + "/ventas/";
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
                return list.size();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener cantidad de ventas: " + e.getMessage());
        }
        return 0;
    }

    public int cantidadDeProductos() {
        try {
            String url = ApiConfig.HOST + "/muebles/";
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
                return list.size();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener cantidad de productos: " + e.getMessage());
        }
        return 0;
    }

    public int cantidadDeEmpleados() {
        try {
            String url = ApiConfig.HOST + "/empleados/";
            HttpRequest req = ApiClient.jsonRequest(url).GET().build();
            HttpResponse<String> respuesta = ApiClient.get().send(req, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                List<?> list = mapper.readValue(respuesta.body(), new TypeReference<List<Object>>() {});
                return list.size();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener cantidad de empleados: " + e.getMessage());
        }
        return 0;
    }
}
