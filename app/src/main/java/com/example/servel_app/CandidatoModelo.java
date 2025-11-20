package com.example.servel_app;

public class CandidatoModelo {
    private String nombre;
    private String partido;

    public CandidatoModelo(String nombre, String partido) {
        this.nombre = nombre;
        this.partido = partido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPartido() {
        return partido;
    }
}
