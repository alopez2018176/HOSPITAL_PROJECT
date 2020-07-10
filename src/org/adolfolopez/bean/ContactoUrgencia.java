package org.adolfolopez.bean;

public class ContactoUrgencia {
    private int codigoContactoUrgencia;
    private String apellidos;
    private String nombres;
    private String numeroContacto;
    private int codigoPaciente;

    public ContactoUrgencia() {
    }

    public ContactoUrgencia(int codigoContactoUrgencia, String apellidos, String nombres, String numeroContacto, int codigoPaciente) {
        this.codigoContactoUrgencia = codigoContactoUrgencia;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.numeroContacto = numeroContacto;
        this.codigoPaciente = codigoPaciente;
    }

    public int getCodigoContactoUrgencia() {
        return codigoContactoUrgencia;
    }

    public void setCodigoContactoUrgencia(int codigoContactoUrgencia) {
        this.codigoContactoUrgencia = codigoContactoUrgencia;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }
    
    
}