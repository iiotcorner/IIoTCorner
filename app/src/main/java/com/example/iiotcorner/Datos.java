package com.example.iiotcorner;

import java.io.Serializable;

public class Datos {

    private String nombre;
    private String accion;
    private double activador;
    private String emails;
    private String contenido;
    private String accionB;
    private double activadorB;
    private String emailB;
    private String contenidoB;

    public Datos(String nombre, String accion, double activador, String emails, String contenido, String accionB,
                 double activadorB, String emailB, String contenidoB) {
        this.nombre = nombre;
        this.accion = accion;
        this.activador = activador;
        this.emails = emails;
        this.contenido = contenido;
        this.accionB = accionB;
        this.activadorB = activadorB;
        this.emailB = emailB;
        this.contenidoB = contenidoB;
    }
    public Datos(Serializable datos){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public double getActivador() {
        return activador;
    }

    public void setActivador(double activador) {
        this.activador = activador;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }


    public String getAccionB() {
        return accionB;
    }

    public void setAccionB(String accionB) {
        this.accionB = accionB;
    }

    public double getActivadorB() {
        return activadorB;
    }

    public void setActivadorB(double activadorB) {
        this.activadorB = activadorB;
    }

    public String getEmailB() {
        return emailB;
    }

    public void setEmailB(String emailB) {
        this.emailB = emailB;
    }

    public String getContenidoB() {
        return contenidoB;
    }

    public void setContenidoB(String contenidoB) {
        this.contenidoB = contenidoB;
    }

}
