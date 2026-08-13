package com.ibk.core.business;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ListarClienteInputPort;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.port.out.persistence.ListarClientesOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@UseCaseService
public class ListarClienteUseCase implements ListarClienteInputPort {

    private final BuscarClienteOutputPort buscarClienteOutputPort;
    private final ListarClientesOutputPort listarClientesOutputPort;

    public ListarClienteUseCase(BuscarClienteOutputPort buscarClienteOutputPort, ListarClientesOutputPort listarClientesOutputPort) {
        this.buscarClienteOutputPort = buscarClienteOutputPort;
        this.listarClientesOutputPort = listarClientesOutputPort;
    }

    @Override
    public Flux<Cliente> listarCliente(RegisterUserCommand headers) {
        return listarClientesOutputPort.listar();
    }

    @Override
    public Mono<Cliente> listarPorIdCliente(RegisterUserCommand headers, String id) {
        return buscarClienteOutputPort.listarPorId(id)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id)));
    }
}
