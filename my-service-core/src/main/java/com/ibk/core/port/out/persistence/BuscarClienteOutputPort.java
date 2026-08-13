package com.ibk.core.port.out.persistence;

import com.ibk.core.model.Cliente;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BuscarClienteOutputPort {
    Mono<Cliente> buscarPorId(UUID id);

}
