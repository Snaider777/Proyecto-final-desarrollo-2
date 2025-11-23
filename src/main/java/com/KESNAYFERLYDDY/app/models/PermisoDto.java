package com.KESNAYFERLYDDY.app.models;

public class PermisoDto {
    private Integer idPermiso;
    private String permiso;

    public Integer getIdPermiso(){ return idPermiso; }
    public void setIdPermiso(Integer v){ this.idPermiso = v; }
    public String getPermiso(){ return permiso; }
    public void setPermiso(String s){ this.permiso = s; }

    @Override
    public String toString() {
        return permiso;
    }
    
}
