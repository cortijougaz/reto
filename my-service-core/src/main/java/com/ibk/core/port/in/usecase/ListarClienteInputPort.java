package com.ibk.core.port.in.usecase;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListarClienteInputPort {
    Flux<Cliente> listarCliente(RegisterUserCommand headers);

    Mono<Cliente> listarPorIdCliente(RegisterUserCommand headers, String id);
}
