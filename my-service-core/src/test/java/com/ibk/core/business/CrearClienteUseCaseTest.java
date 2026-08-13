package com.ibk.core.business;

import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.CrearClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CrearClienteUseCaseTest {

    private CrearClienteOutputPort outputPort;
    private AuditoriaPublisherOutputPort auditoriaPort;
    private CrearClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        outputPort = mock(CrearClienteOutputPort.class);
        auditoriaPort = mock(AuditoriaPublisherOutputPort.class);
        useCase = new CrearClienteUseCase(outputPort, auditoriaPort);
    }

    @Test
    void debeCompletarFechaCrearClienteYAuditarResultado() {
        Cliente entrada = ClienteFixtures.clienteEntrada();
        Cliente persistido = ClienteFixtures.clientePersistido("id-generado");
        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        LocalDateTime antes = LocalDateTime.now();
        when(outputPort.crear(any())).thenReturn(Mono.just(persistido));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.crearCliente(ClienteFixtures.HEADERS, entrada))
                .expectNext(persistido)
                .verifyComplete();

        verify(outputPort).crear(clienteCaptor.capture());
        Cliente enviadoAPersistencia = clienteCaptor.getValue();
        assertNull(enviadoAPersistencia.getId());
        assertFalse(enviadoAPersistencia.getFechaCreacion().isBefore(antes));
        assertFalse(enviadoAPersistencia.getFechaCreacion().isAfter(LocalDateTime.now()));

        ArgumentCaptor<Auditoria> auditoriaCaptor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(auditoriaCaptor.capture());
        Auditoria auditoria = auditoriaCaptor.getValue();
        assertSame(entrada, auditoria.inbound());
        assertSame(persistido, auditoria.outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, auditoria.status());
        assertEquals(TransactionCodeEnum.REGISTRO_CLIENTE, auditoria.transaction());
    }

    @Test
    void debeAuditarYPropagarErrorDePersistencia() {
        Cliente entrada = ClienteFixtures.clienteEntrada();
        RuntimeException fallo = new RuntimeException("mongo caído");
        when(outputPort.crear(any())).thenReturn(Mono.error(fallo));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.crearCliente(ClienteFixtures.HEADERS, entrada))
                .expectErrorMatches(error -> error == fallo)
                .verify();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(StatusCodeEnum.STATUS_DESCONOCIDO, captor.getValue().status());
        assertSame(entrada, captor.getValue().inbound());
        assertNull(captor.getValue().outbound());
    }
}
