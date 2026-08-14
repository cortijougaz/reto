package com.ibk.core.business;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.core.port.in.usecase.CrearClienteInputPort;
import com.ibk.core.port.out.persistence.CrearClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import com.ibk.core.util.UseCaseService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@UseCaseService
public class CrearClienteUseCase implements CrearClienteInputPort {

    private final CrearClienteOutputPort crearClienteOutputPort;
    private final AuditoriaPublisherOutputPort auditoriaPublisherOutputPort;

    public CrearClienteUseCase(
            CrearClienteOutputPort crearClienteOutputPort,
            AuditoriaPublisherOutputPort auditoriaPublisherOutputPort
    ) {
        this.crearClienteOutputPort = crearClienteOutputPort;
        this.auditoriaPublisherOutputPort = auditoriaPublisherOutputPort;
    }

    @Override
    public Mono<Cliente> crearCliente(RegisterUserCommand headers, Cliente cliente) {
        Cliente nuevoCliente = new Cliente(
                null,
                cliente.getNombre(),
                cliente.getApellidoPaterno(),
                cliente.getApellidoMaterno(),
                LocalDateTime.now(),
                cliente.isEstado()
        );

        return crearClienteOutputPort.crear(nuevoCliente)
                .flatMap(clienteCreado ->
                        publicarAuditoriaBestEffort(
                                headers,
                                cliente,
                                clienteCreado,
                                StatusCodeEnum.STATUS_CORRECTO
                        ).thenReturn(clienteCreado)
                )
                .onErrorResume(error ->
                        publicarAuditoriaError(headers, cliente)
                                .then(Mono.error(error))
                );
    }

    private Mono<Void> publicarAuditoria(
            RegisterUserCommand headers,
            Cliente inbound,
            Cliente outbound,
            StatusCodeEnum status
    ) {
        Auditoria auditoria = new Auditoria(
                headers.consumerId(),
                headers.traceId(),
                inbound,
                outbound,
                RegionEnum.ESTE_EEUU_2,
                status,
                TransactionCodeEnum.REGISTRO_CLIENTE,
                headers.deviceId(),
                headers.deviceType()
        );

        return auditoriaPublisherOutputPort.publicar(auditoria);
    }

    private Mono<Void> publicarAuditoriaError(
            RegisterUserCommand headers,
            Cliente inbound
    ) {
        return publicarAuditoriaBestEffort(
                headers,
                inbound,
                null,
                StatusCodeEnum.STATUS_DESCONOCIDO
        );
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
