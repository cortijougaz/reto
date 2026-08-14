package com.ibk.core.business;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.EliminarClienteInputPort;
import com.ibk.core.port.out.persistence.EliminarClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

import java.util.Map;

@UseCaseService
public class EliminarClienteUseCase implements EliminarClienteInputPort {

    private final EliminarClienteOutputPort eliminarClienteOutputPort;
    private final AuditoriaPublisherOutputPort auditoriaPublisherOutputPort;

    public EliminarClienteUseCase(
            EliminarClienteOutputPort eliminarClienteOutputPort,
            AuditoriaPublisherOutputPort auditoriaPublisherOutputPort
    ) {
        this.eliminarClienteOutputPort = eliminarClienteOutputPort;
        this.auditoriaPublisherOutputPort = auditoriaPublisherOutputPort;
    }

    @Override
    public Mono<Void> eliminarPorIdCliente(RegisterUserCommand headers, String id) {
        return eliminarClienteOutputPort.eliminarPorId(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id)))
                .flatMap(resultado ->
                        publicarAuditoriaBestEffort(
                                headers,
                                id,
                                StatusCodeEnum.STATUS_CORRECTO
                        )
                )
                .onErrorResume(error ->
                        publicarAuditoriaError(headers, id)
                                .then(Mono.error(error))
                )
                .then();
    }

    private Mono<Void> publicarAuditoria(
            RegisterUserCommand headers,
            String id,
            StatusCodeEnum status
    ) {
        Auditoria auditoria = new Auditoria(
                headers.consumerId(),
                headers.traceId(),
                Map.of("id", id),
                null,
                RegionEnum.ESTE_EEUU_2,
                status,
                TransactionCodeEnum.BAJA_CLIENTE,
                headers.deviceId(),
                headers.deviceType()
        );

        return auditoriaPublisherOutputPort.publicar(auditoria);
    }

    private Mono<Void> publicarAuditoriaError(
            RegisterUserCommand headers,
            String id
    ) {
        return publicarAuditoriaBestEffort(
                headers,
                id,
                StatusCodeEnum.STATUS_DESCONOCIDO
        );
    }

    private Mono<Void> publicarAuditoriaBestEffort(
            RegisterUserCommand headers,
            String id,
            StatusCodeEnum status
    ) {
        return Mono.defer(() -> publicarAuditoria(headers, id, status))
                .onErrorComplete();
    }
}
