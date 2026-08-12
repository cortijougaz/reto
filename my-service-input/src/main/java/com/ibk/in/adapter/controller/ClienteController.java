package com.ibk.in.adapter.controller;

import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.ClienteResponse;
import com.ibk.in.adapter.dto.HeaderRequest;
import com.ibk.in.adapter.mapper.ClienteMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cliente")
@Slf4j
public class ClienteController {

    private final ActualizarClienteInputPort actualizarClienteInputPort;
    private final ClienteMapper clienteMapper;

    @PutMapping("/actualizar/{id}")
    public Mono<ResponseEntity<ClienteResponse>> actualizarCliente(HeaderRequest headers,
                                                                  @PathVariable(name = "id") UUID id,
                                                                  @Valid @RequestBody ClienteRequest cliente) {
        var command = clienteMapper.toRegisterUserCommand(headers);

        return actualizarClienteInputPort.actualizarCliente(command, id, clienteMapper.toDomain(cliente))
                .map(clienteMapper::toResponse)
                .map(clienteResponse -> ResponseEntity.status(HttpStatus.OK).body(clienteResponse));
    }
}
