package com.KESNAYFERLYDDY.app.models;

import java.math.BigDecimal;

public class DetalleVentasDto {
    private Integer idDetalleVenta;
    private Integer muebleId;
    private MuebleDto mueble;
    private Integer cantidad;
    private BigDecimal subTotal;
    private BigDecimal precio;
    private VentaDto venta;
    
    public Integer getIdDetalleVenta() {return idDetalleVenta;}
    public void setIdDetalleVenta(Integer idDetalleVenta) {this.idDetalleVenta = idDetalleVenta;}
    public Integer getMuebleId() {return muebleId;}
    public void setMuebleId(Integer muebleId) {this.muebleId = muebleId;}
    public MuebleDto getMueble() {return mueble;}
    public void setMueble(MuebleDto mueble) {this.mueble = mueble;}
    public Integer getCantidad() {return cantidad;}
    public void setCantidad(Integer cantidad) {this.cantidad = cantidad;}
    public BigDecimal getSubTotal() {return subTotal;}
    public void setSubTotal(BigDecimal subTotal) {this.subTotal = subTotal;}
    public BigDecimal getPrecio() {return precio;}
    public void setPrecio(BigDecimal precio) {this.precio = precio;}
    public VentaDto getVenta() {return venta;}
    public void setVenta(VentaDto venta) {this.venta = venta;}
}
