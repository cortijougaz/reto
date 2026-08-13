package com.ibk.core.port.out.persistence;

import com.ibk.core.model.Cliente;
import reactor.core.publisher.Mono;

public interface ActualizarClienteOutputPort {

    Mono<Cliente> actualizarCliente(String id, Cliente cliente);
}
