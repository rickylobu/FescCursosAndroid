package com.example.fesccursos.model;

import java.util.List;

public class Curso {
    private String idCurso;
    private String idUsuario;
    private String nombre;
    private String categoria;
    private String descripcion;
    private  String imagen;

    //FireBase necesita constructor vacio para descerializar el objeto Curso
    public Curso() { }

    // Constructor
    public Curso(String idCurso, String idUsuario, String nombre, String categoria, String descripcion, String imagen) {
        this.idCurso = idCurso;
        this.idUsuario=idUsuario;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.imagen =imagen;
    }

    // Getters y Setters
    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }

    public String getIdUsuario() { return idUsuario; }

    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() { return imagen; }

    public void setImagen(String imagen) { this.imagen = imagen; }


}
