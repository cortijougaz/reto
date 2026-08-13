package com.ibk.core.business;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;

import java.time.LocalDateTime;

final class ClienteFixtures {

    static final RegisterUserCommand HEADERS = new RegisterUserCommand(
            "SMP",
            "00-db65adadcc7ab67b6eaa38521c34c42a-0123456789abcdef-01",
            "IPH",
            "device-1"
    );

    private ClienteFixtures() {
    }

    static Cliente clienteEntrada() {
        return new Cliente(null, "Juan", "Pérez", "Gómez", null, true);
    }

    static Cliente clientePersistido(String id) {
        return new Cliente(
                id,
                "Juan",
                "Pérez",
                "Gómez",
                LocalDateTime.of(2026, 8, 13, 16, 30),
                true
        );
    }
}
