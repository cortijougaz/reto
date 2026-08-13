package com.ibk.core.port.out.publisher;

import com.ibk.core.model.Auditoria;
import reactor.core.publisher.Mono;

public interface AuditoriaPublisherOutputPort {
    Mono<Void> publicar(Auditoria auditoria);
}
