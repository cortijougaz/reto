package com.ibk.core.port.in.usecase;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import reactor.core.publisher.Mono;

public interface EliminarClienteInputPort {

    Mono<Void> eliminarPorIdCliente(RegisterUserCommand headers, String id);
}
