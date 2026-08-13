package com.ibk.core.port.out.persistence;

import reactor.core.publisher.Mono;

public interface EliminarClienteOutputPort {
    Mono<Boolean> eliminarPorId(String id);
}
