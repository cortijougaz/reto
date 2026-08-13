package com.ibk.core.port.out.persistence;

import com.ibk.core.model.Cliente;
import reactor.core.publisher.Mono;

public interface BuscarClienteOutputPort {
    Mono<Cliente> listarPorId(String id);

}
