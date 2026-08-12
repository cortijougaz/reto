package com.ibk.in.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequest(
        @NotBlank(message = "El nombre no puede ser nulo ni estar vacío")
        String nombre,

        @NotBlank(message = "El apellido paterno no puede ser nulo ni estar vacío")
        String apellidoPaterno,

        @NotNull(message = "El apellido materno puede estar vacío, pero no ser nulo")
        String apellidoMaterno,

        @NotNull(message = "Se tiene que informar un estado")
        Boolean estado
) {
}
