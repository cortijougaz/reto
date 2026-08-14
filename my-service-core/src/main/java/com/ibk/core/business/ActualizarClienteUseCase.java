package com.ibk.core.business;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

@UseCaseService
public class ActualizarClienteUseCase implements ActualizarClienteInputPort {

    private final ActualizarClienteOutputPort actualizarClienteOutputPort;
    private final AuditoriaPublisherOutputPort auditoriaPublisherOutputPort;

    ActualizarClienteUseCase(ActualizarClienteOutputPort actualizarClienteOutputPort, AuditoriaPublisherOutputPort auditoriaPublisherOutputPort) {
        this.actualizarClienteOutputPort = actualizarClienteOutputPort;
        this.auditoriaPublisherOutputPort = auditoriaPublisherOutputPort;
    }

    @Override
    public Mono<Cliente> actualizarCliente(RegisterUserCommand headers, String id, Cliente cliente) {
        return actualizarClienteOutputPort.actualizarCliente(id, cliente)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id
                        )
                ))
                .flatMap(resultado ->
                        publicarAuditoriaBestEffort(
                                headers,
                                cliente,
                                resultado,
                                StatusCodeEnum.STATUS_CORRECTO
                        ).thenReturn(resultado)
                )
                .onErrorResume(error ->
                        publicarAuditoriaBestEffort(
                                headers,
                                cliente,
                                null,
                                StatusCodeEnum.STATUS_DESCONOCIDO
                        ).then(Mono.error(error))
                );
    }

    private Mono<Void> publicarAuditoria(
            RegisterUserCommand headers,
            Cliente inbound,
            Cliente outbound,
            StatusCodeEnum status) {
        Auditoria auditoria = new Auditoria(
                headers.consumerId(),
                headers.traceId(),
                inbound,
                outbound,
                RegionEnum.ESTE_EEUU_2,
                status,
                TransactionCodeEnum.ACTUALIZACION_CLIENTE,
                headers.deviceId(),
                headers.deviceType()
        );

        return auditoriaPublisherOutputPort.publicar(auditoria);
    }

    private Mono<Void> publicarAuditoriaBestEffort(
            RegisterUserCommand headers,
            Cliente inbound,
            Cliente outbound,
            StatusCodeEnum status
    ) {
        return Mono.defer(() -> publicarAuditoria(headers, inbound, outbound, status))
                .onErrorComplete();
    }
}
