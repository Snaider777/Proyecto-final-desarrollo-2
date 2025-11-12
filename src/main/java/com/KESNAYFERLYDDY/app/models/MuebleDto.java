package com.KESNAYFERLYDDY.app.models;

public class MuebleDto {
    private Integer idMueble;
    private String nombreMueble;
    private CategoriaDto categoria;
    private String nombreMaterial;
    private String descripcionMueble;
    private Integer existencia;
    private String estado;

    public Integer getIdMueble() {
        return idMueble;
    }

    public void setIdMueble(Integer idMueble) {
        this.idMueble = idMueble;
    }

    public String getNombreMueble() {
        return nombreMueble;
    }

    public void setNombreMueble(String nombreMueble) {
        this.nombreMueble = nombreMueble;
    }

    public CategoriaDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDto categoria) {
        this.categoria = categoria;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public String getDescripcionMueble() {
        return descripcionMueble;
    }

    public void setDescripcionMueble(String descripcionMueble) {
        this.descripcionMueble = descripcionMueble;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
