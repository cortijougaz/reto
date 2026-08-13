package com.ibk.core.business;

import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActualizarClienteUseCaseTest {

    private ActualizarClienteOutputPort outputPort;
    private AuditoriaPublisherOutputPort auditoriaPort;
    private ActualizarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        outputPort = mock(ActualizarClienteOutputPort.class);
        auditoriaPort = mock(AuditoriaPublisherOutputPort.class);
        useCase = new ActualizarClienteUseCase(outputPort, auditoriaPort);
    }

    @Test
    void debeActualizarYPublicarAuditoriaExitosa() {
        Cliente entrada = ClienteFixtures.clienteEntrada();
        Cliente actualizado = ClienteFixtures.clientePersistido("id-1");
        when(outputPort.actualizarCliente("id-1", entrada)).thenReturn(Mono.just(actualizado));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.actualizarCliente(ClienteFixtures.HEADERS, "id-1", entrada))
                .expectNext(actualizado)
                .verifyComplete();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        Auditoria auditoria = captor.getValue();
        assertSame(entrada, auditoria.inbound());
        assertSame(actualizado, auditoria.outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, auditoria.status());
        assertEquals(TransactionCodeEnum.ACTUALIZACION_CLIENTE, auditoria.transaction());
    }

    @Test
    void debeEmitirNoEncontradoYPublicarAuditoriaDeError() {
        Cliente entrada = ClienteFixtures.clienteEntrada();
        when(outputPort.actualizarCliente("ausente", entrada)).thenReturn(Mono.empty());
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.actualizarCliente(ClienteFixtures.HEADERS, "ausente", entrada))
                .expectErrorSatisfies(error -> {
                    assertEquals(RecursoNoEncontradoException.class, error.getClass());
                    assertEquals("No se encontró el cliente con ID: ausente", error.getMessage());
                })
                .verify();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(StatusCodeEnum.STATUS_DESCONOCIDO, captor.getValue().status());
        assertSame(entrada, captor.getValue().inbound());
        assertNull(captor.getValue().outbound());
    }

    @Test
    void debePropagarFalloDeAuditoriaObligatoria() {
        Cliente entrada = ClienteFixtures.clienteEntrada();
        Cliente actualizado = ClienteFixtures.clientePersistido("id-1");
        IllegalStateException falloAuditoria = new IllegalStateException("service bus caído");
        AtomicInteger publicaciones = new AtomicInteger();
        when(outputPort.actualizarCliente("id-1", entrada)).thenReturn(Mono.just(actualizado));
        when(auditoriaPort.publicar(any()))
                .thenAnswer(invocation -> publicaciones.getAndIncrement() == 0
                        ? Mono.error(falloAuditoria)
                        : Mono.empty());

        StepVerifier.create(useCase.actualizarCliente(ClienteFixtures.HEADERS, "id-1", entrada))
                .expectErrorMatches(error -> error == falloAuditoria)
                .verify();

        verify(auditoriaPort, times(2)).publicar(any());
    }
}
