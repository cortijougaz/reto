package com.ibk.core.port.in.usecase;

import com.ibk.core.model.Cliente;

import java.util.UUID;

public interface ActualizarClienteInputPort {
    Cliente actualizarCliente(HeaderRequest headers, UUID id, Cliente cliente);
}
