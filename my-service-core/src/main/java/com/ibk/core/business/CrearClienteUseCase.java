package com.ibk.core.business;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.CrearClienteInputPort;
import com.ibk.core.port.out.persistence.CrearClienteOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@UseCaseService
public class CrearClienteUseCase implements CrearClienteInputPort {

    private final CrearClienteOutputPort crearClienteOutputPort;

    public CrearClienteUseCase(CrearClienteOutputPort crearClienteOutputPort) {
        this.crearClienteOutputPort = crearClienteOutputPort;
    }

    @Override
    public Mono<Cliente> crearCliente(RegisterUserCommand headers, Cliente cliente) {
        Cliente nuevoCliente = new Cliente(
                null,
                cliente.getNombre(),
                cliente.getApellidoPaterno(),
                cliente.getApellidoMaterno(),
                LocalDate.now(),
                cliente.isEstado()
        );

        return crearClienteOutputPort.crear(nuevoCliente);
    }
}
