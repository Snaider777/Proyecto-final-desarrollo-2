package com.KESNAYFERLYDDY.app.models;

public class EmpleadosDto {
    private Integer idEmpleado;
    private String apellidosEmpleado;
    private String direccionEmpleado;
    private String duiEmpleado;
    private String nombresEmpleado;
    private String telefonoEmpleado;

    // getters y setters
    public Integer getIdEmpleado(){ return idEmpleado; }
    public void setIdEmpleado(Integer id){ this.idEmpleado = id; }
    public String getNombresEmpleado(){ return nombresEmpleado; }
    public void setNombresEmpleado(String s){ this.nombresEmpleado = s; }
    public String getApellidosEmpleado(){ return apellidosEmpleado; }
    public void setApellidosEmpleado(String s){ this.apellidosEmpleado = s; }
    public String getTelefonoEmpleado(){ return telefonoEmpleado; }
    public void setTelefonoEmpleado(String s){ this.telefonoEmpleado = s; }
    public String getDuiEmpleado(){ return duiEmpleado; }
    public void setDuiEmpleado(String s){ this.duiEmpleado = s; }
    public String getDireccionEmpleado(){ return direccionEmpleado; }
    public void setDireccionEmpleado(String s){ this.direccionEmpleado = s; }
}
