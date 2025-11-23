package com.KESNAYFERLYDDY.app.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MuebleVendidoDto {
    @JsonProperty("mueble")
    private MuebleDto mueble;

    @JsonProperty("cantidadVendida")
    private int cantidadVendida;

    public MuebleDto getMueble() {
        return mueble;
    }

    public void setMueble(MuebleDto mueble) {
        this.mueble = mueble;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}
