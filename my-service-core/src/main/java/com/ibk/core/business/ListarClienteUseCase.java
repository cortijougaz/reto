package com.ibk.core.business;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.ListarClienteInputPort;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.port.out.persistence.ListarClientesOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@UseCaseService
public class ListarClienteUseCase implements ListarClienteInputPort {

    private final BuscarClienteOutputPort buscarClienteOutputPort;
    private final ListarClientesOutputPort listarClientesOutputPort;
    private final AuditoriaPublisherOutputPort auditoriaPublisherOutputPort;

    public ListarClienteUseCase(
            BuscarClienteOutputPort buscarClienteOutputPort,
            ListarClientesOutputPort listarClientesOutputPort,
            AuditoriaPublisherOutputPort auditoriaPublisherOutputPort
    ) {
        this.buscarClienteOutputPort = buscarClienteOutputPort;
        this.listarClientesOutputPort = listarClientesOutputPort;
        this.auditoriaPublisherOutputPort = auditoriaPublisherOutputPort;
    }

    @Override
    public Flux<Cliente> listarCliente(RegisterUserCommand headers) {
        return listarClientesOutputPort.listar()
                .collectList()
                .flatMap(clientes ->
                        publicarAuditoria(
                                headers,
                                clientes,
                                StatusCodeEnum.STATUS_CORRECTO
                        ).thenReturn(clientes)
                )
                .onErrorResume(error ->
                        publicarAuditoriaError(headers)
                                .then(Mono.error(error))
                )
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Cliente> listarPorIdCliente(RegisterUserCommand headers, String id) {
        return buscarClienteOutputPort.listarPorId(id)
                .switchIfEmpty(Mono.error(
                        new RecursoNoEncontradoException(
                                "No se encontró el cliente con ID: " + id)))
                .flatMap(cliente ->
                        publicarAuditoria(
                                headers,
                                cliente,
                                StatusCodeEnum.STATUS_CORRECTO
                        ).thenReturn(cliente)
                )
                .onErrorResume(error ->
                        publicarAuditoriaError(headers)
                                .then(Mono.error(error))
                );
    }

    private Mono<Void> publicarAuditoria(
            RegisterUserCommand headers,
            Object outbound,
            StatusCodeEnum status
    ) {
        Auditoria auditoria = new Auditoria(
                headers.consumerId(),
                headers.traceId(),
                null,
                outbound,
                RegionEnum.ESTE_EEUU_2,
                status,
                TransactionCodeEnum.CONSULTA_CLIENTE,
                headers.deviceId(),
                headers.deviceType()
        );

        return auditoriaPublisherOutputPort.publicar(auditoria);
    }

    private Mono<Void> publicarAuditoriaError(RegisterUserCommand headers) {
        return publicarAuditoria(
                headers,
                null,
                StatusCodeEnum.STATUS_DESCONOCIDO
        ).onErrorResume(errorAuditoria -> Mono.empty());
    }
}
