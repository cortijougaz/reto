package com.ibk.core.port.out.persistence;

import com.ibk.core.model.Cliente;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActualizarClienteOutputPort {

    Mono<Cliente> actualizarCliente(UUID id, Cliente cliente);
}
