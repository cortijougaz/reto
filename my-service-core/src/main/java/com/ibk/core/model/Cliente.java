package com.ibk.core.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Cliente {

    private UUID id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaCreacion;
    private boolean estado;

    public Cliente() {
    }

    public Cliente(UUID id, String nombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechaCreacion, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return estado == cliente.estado && Objects.equals(id, cliente.id) && Objects.equals(nombre, cliente.nombre) && Objects.equals(apellidoPaterno, cliente.apellidoPaterno) && Objects.equals(apellidoMaterno, cliente.apellidoMaterno) && Objects.equals(fechaCreacion, cliente.fechaCreacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellidoPaterno, apellidoMaterno, fechaCreacion, estado);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", estado=" + estado +
                '}';
    }
}
