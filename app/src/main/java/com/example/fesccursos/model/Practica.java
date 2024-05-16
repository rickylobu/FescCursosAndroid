package com.example.fesccursos.model;

import static java.lang.String.valueOf;

public class Practica {
    private String idPractica;
    private String idCurso;
    private int numPractica;
    private String nombre;
    private String descripcion;
    private String video;

    //FireBase necesita constructor vacio para descerializar el objeto Práctica
    public Practica() { }


    // Constructor
    public Practica(String idPractica, String idCurso,int numPractica, String nombre, String descripcion, String video) {
        this.idPractica = idPractica;
        this.idCurso = idCurso;
        this.numPractica=numPractica;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.video = video;
    }

    // Getters y Setters
    public String getIdPractica() {
        return idPractica;
    }

    public void setIdPractica(String idPractica) {
        this.idPractica = idPractica;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }

    public Object getNumPractica() {
        return numPractica != 0 ? Integer.valueOf(numPractica) : "";
    }

    public void setNumPractica(int numPractica) {
        this.numPractica = numPractica;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}

