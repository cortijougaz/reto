package com.ibk.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterUserCommandTest {

    @Test
    void debeExtraerTraceIdDeTraceparentValido() {
        RegisterUserCommand command = new RegisterUserCommand(
                "SMP",
                "00-db65adadcc7ab67b6eaa38521c34c42a-0123456789abcdef-01",
                "IPH",
                "device-1"
        );

        assertEquals("db65adadcc7ab67b6eaa38521c34c42a", command.traceId());
    }

    @Test
    void debeRetornarVacioCuandoTraceparentEsNuloOVacio() {
        assertEquals("", new RegisterUserCommand("SMP", null, "IPH", "1").traceId());
        assertEquals("", new RegisterUserCommand("SMP", "   ", "IPH", "1").traceId());
    }

    @Test
    void debeRetornarValorOriginalCuandoFormatoNoTieneTraceId() {
        RegisterUserCommand command = new RegisterUserCommand("SMP", "invalido", "IPH", "1");

        assertEquals("invalido", command.traceId());
    }
}
