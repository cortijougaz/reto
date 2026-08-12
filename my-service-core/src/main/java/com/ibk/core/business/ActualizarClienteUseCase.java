package com.ibk.core.business;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

@UseCaseService
public class ActualizarClienteUseCase implements ActualizarClienteInputPort {

    private final ActualizarClienteOutputPort actualizarClienteOutputPort;

    ActualizarClienteUseCase(ActualizarClienteOutputPort actualizarClienteOutputPort) {
        this.actualizarClienteOutputPort = actualizarClienteOutputPort;
    }

    @Override
    public Mono<Cliente> actualizarCliente(RegisterUserCommand headers, UUID id, Cliente cliente) {
        return actualizarClienteOutputPort.actualizarCliente(id, cliente);
    }
}
