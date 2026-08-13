package com.ibk.in.adapter.controller;

import com.ibk.core.business.CrearClienteUseCase;
import com.ibk.core.business.EliminarClienteUseCase;
import com.ibk.core.business.ListarClienteUseCase;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.in.usecase.CrearClienteInputPort;
import com.ibk.core.port.in.usecase.EliminarClienteInputPort;
import com.ibk.core.port.in.usecase.ListarClienteInputPort;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.ClienteResponse;
import com.ibk.in.adapter.dto.HeaderRequest;
import com.ibk.in.adapter.mapper.ClienteMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cliente")
@Slf4j
public class ClienteController {

    private final ActualizarClienteInputPort actualizarClienteInputPort;
    private final CrearClienteInputPort crearClienteInputPort;
    private final EliminarClienteInputPort eliminarClienteInputPort;
    private final ListarClienteInputPort listarClienteInputPort;
    private final ClienteMapper clienteMapper;

    @PutMapping("/actualizar/{id}")
    public Mono<ResponseEntity<ClienteResponse>> actualizarCliente(HeaderRequest headers,
                                                                  @PathVariable(name = "id") String id,
                                                                  @Valid @RequestBody ClienteRequest cliente) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return actualizarClienteInputPort.actualizarCliente(command, id, clienteMapper.toDomain(cliente))
                .map(clienteMapper::toResponse)
                .map(clienteResponse -> ResponseEntity.status(HttpStatus.OK).body(clienteResponse));
    }

    @PutMapping("/crear")
    public Mono<ResponseEntity<ClienteResponse>> crearCliente(HeaderRequest headers,
                                                                   @Valid @RequestBody ClienteRequest cliente) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return crearClienteInputPort.crearCliente(command, clienteMapper.toDomain(cliente))
                .map(clienteMapper::toResponse)
                .map(clienteResponse -> ResponseEntity.status(HttpStatus.OK).body(clienteResponse));
    }

    @GetMapping("/buscar/{id}")
    public Mono<ResponseEntity<ClienteResponse>> buscarPorIdCliente(HeaderRequest headers, @PathVariable(name = "id") String id) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return listarClienteInputPort.listarPorIdCliente(command, id)
                .map(clienteMapper::toResponse)
                .map(clienteResponse -> ResponseEntity.status(HttpStatus.OK).body(clienteResponse));
    }

    @GetMapping("/buscar")
    public Flux<ClienteResponse> buscarCliente(HeaderRequest headers) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return listarClienteInputPort.listarCliente(command)
                .map(clienteMapper::toResponse);
    }

    @DeleteMapping("/eliminar/{id}")
    public Mono<ResponseEntity<Void>> eliminarPorIdCliente(HeaderRequest headers, @PathVariable(name = "id") String id) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return eliminarClienteInputPort.eliminarPorIdCliente(command, id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
