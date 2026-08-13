package com.ibk.core.port.out.persistence;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EliminarClienteOutputPort {
    Mono<Boolean> eliminarPorId(UUID id);
}
