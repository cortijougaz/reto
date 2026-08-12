package com.ibk.core.port.in.usecase;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;

import java.util.UUID;

public interface ActualizarClienteInputPort {
    Cliente actualizarCliente(RegisterUserCommand headers, UUID id, Cliente cliente);
}
