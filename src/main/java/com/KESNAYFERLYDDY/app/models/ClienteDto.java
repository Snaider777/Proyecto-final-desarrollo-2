package com.KESNAYFERLYDDY.app.models;

public class ClienteDto {
    private Integer idCliente;
    private String nombresCliente;
    private String apellidosCliente;
    private String telefonoCliente;
    private String duiCliente;
    private String direccionCliente;
    // getters y setters
    public Integer getIdCliente(){ return idCliente; }
    public void setIdCliente(Integer id){ this.idCliente = id; }
    public String getNombresCliente(){ return nombresCliente; }
    public void setNombresCliente(String s){ this.nombresCliente = s; }
    public String getApellidosCliente(){ return apellidosCliente; }
    public void setApellidosCliente(String s){ this.apellidosCliente = s; }
    public String getTelefonoCliente(){ return telefonoCliente; }
    public void setTelefonoCliente(String s){ this.telefonoCliente = s; }
    public String getDuiCliente(){ return duiCliente; }
    public void setDuiCliente(String s){ this.duiCliente = s; }
    public String getDireccionCliente(){ return direccionCliente; }
    public void setDireccionCliente(String s){ this.direccionCliente = s; }
    @Override public String toString() { return nombresCliente;}
}
