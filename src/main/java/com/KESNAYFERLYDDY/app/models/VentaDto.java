package com.KESNAYFERLYDDY.app.models;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaDto {
    private Integer idVenta;
    private Integer clienteId;
    private Integer empleadoId;
    private ClienteDto cliente;
    private EmpleadosDto empleado;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private List<DetalleVentasDto> detalles = new ArrayList<>();
    
    public Integer getIdVenta() {return idVenta;}
    public void setIdVenta(Integer idVenta) {this.idVenta = idVenta;}
    public Integer getClienteId() {return clienteId;}
    public void setClienteId(Integer clienteId) {this.clienteId = clienteId;}
    public Integer getEmpleadoId() {return empleadoId;}
    public void setEmpleadoId(Integer empleadoId) {this.empleadoId = empleadoId;}
    public ClienteDto getCliente() {return cliente;}
    public void setCliente(ClienteDto cliente) {this.cliente = cliente;}
    public EmpleadosDto getEmpleado() {return empleado;}
    public void setEmpleado(EmpleadosDto empleado) {this.empleado = empleado;}
    public LocalDateTime getFechaVenta() {return fechaVenta;}
    public void setFechaVenta(LocalDateTime fechaVenta) {this.fechaVenta = fechaVenta;}
    public BigDecimal getTotal() {return total;}
    public void setTotal(BigDecimal total) {this.total = total;}
    public List<DetalleVentasDto> getDetalles() {return detalles;}
    public void setDetalles(List<DetalleVentasDto> detalles) {this.detalles = detalles;}
}
