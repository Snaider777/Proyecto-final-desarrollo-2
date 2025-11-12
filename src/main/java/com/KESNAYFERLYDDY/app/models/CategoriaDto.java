package com.KESNAYFERLYDDY.app.models;

public class CategoriaDto {
    private Integer idCategoria;
    private String categoria;

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    @Override public String toString() { return categoria;}
}
