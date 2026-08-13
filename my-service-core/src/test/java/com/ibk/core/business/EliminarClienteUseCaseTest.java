package com.ibk.core.business;

import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.core.model.Auditoria;
import com.ibk.core.port.out.persistence.EliminarClienteOutputPort;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EliminarClienteUseCaseTest {

    private EliminarClienteOutputPort outputPort;
    private AuditoriaPublisherOutputPort auditoriaPort;
    private EliminarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        outputPort = mock(EliminarClienteOutputPort.class);
        auditoriaPort = mock(AuditoriaPublisherOutputPort.class);
        useCase = new EliminarClienteUseCase(outputPort, auditoriaPort);
    }

    @Test
    void debeEliminarYPublicarAuditoriaExitosa() {
        when(outputPort.eliminarPorId("1")).thenReturn(Mono.just(true));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminarPorIdCliente(ClienteFixtures.HEADERS, "1"))
                .verifyComplete();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(Map.of("id", "1"), captor.getValue().inbound());
        assertNull(captor.getValue().outbound());
        assertEquals(StatusCodeEnum.STATUS_CORRECTO, captor.getValue().status());
        assertEquals(TransactionCodeEnum.BAJA_CLIENTE, captor.getValue().transaction());
    }

    @Test
    void debeEmitirNoEncontradoCuandoNoSeEliminoDocumento() {
        when(outputPort.eliminarPorId("ausente")).thenReturn(Mono.just(false));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminarPorIdCliente(ClienteFixtures.HEADERS, "ausente"))
                .expectError(RecursoNoEncontradoException.class)
                .verify();

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaPort).publicar(captor.capture());
        assertEquals(StatusCodeEnum.STATUS_DESCONOCIDO, captor.getValue().status());
    }

    @Test
    void debePreservarErrorDePersistenciaAunqueFalleAuditoriaDeError() {
        RuntimeException falloMongo = new RuntimeException("mongo caído");
        when(outputPort.eliminarPorId("1")).thenReturn(Mono.error(falloMongo));
        when(auditoriaPort.publicar(any())).thenReturn(Mono.error(new RuntimeException("bus caído")));

        StepVerifier.create(useCase.eliminarPorIdCliente(ClienteFixtures.HEADERS, "1"))
                .expectErrorMatches(error -> error == falloMongo)
                .verify();
    }
}
