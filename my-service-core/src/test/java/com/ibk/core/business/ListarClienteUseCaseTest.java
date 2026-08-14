package com.ibk.core.business;

import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.port.out.persistence.ListarClientesOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListarClienteUseCaseTest {

    private BuscarClienteOutputPort buscarPort;
    private ListarClientesOutputPort listarPort;
    private AuditoriaPublisherOutputPort auditoriaPort;
    private ListarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        buscarPort = mock(BuscarClienteOutputPort.class);
        listarPort = mock(ListarClientesOutputPort.class);
        auditoriaPort = mock(AuditoriaPublisherOutputPort.class);
        useCase = new ListarClienteUseCase(buscarPort, listarPort, auditoriaPort);
    }

    @Test
    void debeListarTodosYAuditarLaListaCompleta() {
        Cliente uno = ClienteFixtures.clientePersistido("1");
        Cliente dos = ClienteFixtures.clientePersistido("2");
        when(listarPort.listar()).thenReturn(Flux.just(uno, dos));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listarCliente(ClienteFixtures.HEADERS))
                .expectNext(uno, dos)
                .verifyComplete();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(List.of(uno, dos), captor.getValue().outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, captor.getValue().status());
        assertEquals(TransactionCodeEnum.CONSULTA_CLIENTE, captor.getValue().transaction());
        assertNull(captor.getValue().inbound());
    }

    @Test
    void debeAuditarListaVaciaComoResultadoExitoso() {
        when(listarPort.listar()).thenReturn(Flux.empty());
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listarCliente(ClienteFixtures.HEADERS))
                .verifyComplete();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(List.of(), captor.getValue().outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, captor.getValue().status());
    }

    @Test
    void debeBuscarPorIdYAuditarCliente() {
        Cliente cliente = ClienteFixtures.clientePersistido("1");
        when(buscarPort.listarPorId("1")).thenReturn(Mono.just(cliente));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listarPorIdCliente(ClienteFixtures.HEADERS, "1"))
                .expectNext(cliente)
                .verifyComplete();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertSame(cliente, captor.getValue().outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, captor.getValue().status());
    }

    @Test
    void debeEmitirNoEncontradoYAuditarError() {
        when(buscarPort.listarPorId("ausente")).thenReturn(Mono.empty());
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.listarPorIdCliente(ClienteFixtures.HEADERS, "ausente"))
                .expectError(RecursoNoEncontradoException.class)
                .verify();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(StatusCodeEnum.STATUS_DESCONOCIDO, captor.getValue().status());
        assertNull(captor.getValue().outbound());
    }

    @Test
    void debeListarClientesAunqueFalleAuditoria() {
        Cliente cliente = ClienteFixtures.clientePersistido("1");
        when(listarPort.listar()).thenReturn(Flux.just(cliente));
        when(auditoriaPort.publicar(any()))
                .thenReturn(Mono.error(new RuntimeException("service bus caido")));

        StepVerifier.create(useCase.listarCliente(ClienteFixtures.HEADERS))
                .expectNext(cliente)
                .verifyComplete();

        verify(auditoriaPort).publicar(any());
    }

    @Test
    void debeRetornarClientePorIdAunqueFalleAuditoria() {
        Cliente cliente = ClienteFixtures.clientePersistido("1");
        when(buscarPort.listarPorId("1")).thenReturn(Mono.just(cliente));
        when(auditoriaPort.publicar(any()))
                .thenReturn(Mono.error(new RuntimeException("service bus caido")));

        StepVerifier.create(useCase.listarPorIdCliente(ClienteFixtures.HEADERS, "1"))
                .expectNext(cliente)
                .verifyComplete();

        verify(auditoriaPort).publicar(any());
    }
}
