package com.ibk.core.port.out.persistence;

import com.ibk.core.model.Cliente;
import reactor.core.publisher.Flux;

public interface ListarClientesOutputPort {
    Flux<Cliente> listar();
}
