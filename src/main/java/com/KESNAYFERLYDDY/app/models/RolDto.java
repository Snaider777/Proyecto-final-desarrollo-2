package com.KESNAYFERLYDDY.app.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RolDto {
    private Integer idRol;
    private String rol;
    
    public Integer getIdRol(){ return idRol; }
    public void setIdRol(Integer v){ this.idRol = v; }
    public String getRol(){ return rol; }
    public void setRol(String s){ this.rol = s; }

    @Override
    public String toString() {
        return rol;
    }
}