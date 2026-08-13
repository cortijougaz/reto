package com.ibk.core.business;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

@UseCaseService
public class ActualizarClienteUseCase implements ActualizarClienteInputPort {

    private final BuscarClienteOutputPort buscarClienteOutputPort;
    private final ActualizarClienteOutputPort actualizarClienteOutputPort;

    ActualizarClienteUseCase(BuscarClienteOutputPort buscarClienteOutputPort,
                             ActualizarClienteOutputPort actualizarClienteOutputPort) {
        this.actualizarClienteOutputPort = actualizarClienteOutputPort;
        this.buscarClienteOutputPort =buscarClienteOutputPort;
    }

    @Override
    public Mono<Cliente> actualizarCliente(RegisterUserCommand headers, UUID id, Cliente cliente) {
        return buscarClienteOutputPort.buscarPorId(id)
                .map(clienteExiste -> aplicarCambios(clienteExiste, cliente))
                .flatMap(clienteActualizado -> actualizarClienteOutputPort.actualizarCliente(id, clienteActualizado))
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id
                        )
                ));
    }


    private Cliente aplicarCambios(Cliente clienteExistente, Cliente cambios) {
        return new Cliente(
                clienteExistente.getId(),
                cambios.getNombre(),
                cambios.getApellidoPaterno(),
                cambios.getApellidoMaterno(),
                clienteExistente.getFechaCreacion(),
                cambios.isEstado()
        );
    }
}
