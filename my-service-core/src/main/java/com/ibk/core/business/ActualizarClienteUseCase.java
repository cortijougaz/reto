package com.ibk.core.business;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

@UseCaseService
public class ActualizarClienteUseCase implements ActualizarClienteInputPort {

    private final ActualizarClienteOutputPort actualizarClienteOutputPort;

    ActualizarClienteUseCase(ActualizarClienteOutputPort actualizarClienteOutputPort) {
        this.actualizarClienteOutputPort = actualizarClienteOutputPort;
    }

    @Override
    public Mono<Cliente> actualizarCliente(RegisterUserCommand headers, String id, Cliente cliente) {
        return actualizarClienteOutputPort.actualizarCliente(id, cliente)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id
                        )
                ));
    }
}
