package com.ibk.core.business;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.EliminarClienteInputPort;
import com.ibk.core.port.out.persistence.EliminarClienteOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

@UseCaseService
public class EliminarClienteUseCase implements EliminarClienteInputPort {

    private final EliminarClienteOutputPort eliminarClienteOutputPort;

    public EliminarClienteUseCase(EliminarClienteOutputPort eliminarClienteOutputPort) {
        this.eliminarClienteOutputPort = eliminarClienteOutputPort;
    }

    @Override
    public Mono<Void> eliminarPorIdCliente(RegisterUserCommand headers, String id) {
        return eliminarClienteOutputPort.eliminarPorId(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id)))
                .then();
    }
}
