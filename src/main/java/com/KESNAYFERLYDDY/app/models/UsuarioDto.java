package com.KESNAYFERLYDDY.app.models;
public class UsuarioDto {
    private Integer idUsuario;
    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private Integer idEmpleado; 

    public Integer getIdUsuario(){ return idUsuario; }
    public void setIdUsuario(Integer v){ this.idUsuario = v; }
    public String getNombreUsuario(){ return nombreUsuario; }
    public void setNombreUsuario(String s){ this.nombreUsuario = s; }
    public String getCorreo(){ return correo; }
    public void setCorreo(String s){ this.correo = s; }
    public String getContrasena(){ return contrasena; }
    public void setContrasena(String s){ this.contrasena = s; }
    public Integer getIdEmpleado(){ return idEmpleado; }
    public void setIdEmpleado(Integer id){ this.idEmpleado = id; }
}

