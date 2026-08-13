package com.ibk.out.publisher;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.model.Auditoria;
import com.ibk.out.config.AuditProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceBusPublisherAdapterTest {

    private JsonAuditSerializer serializer;
    private AuditProperties properties;
    private ServiceBusPublisherAdapter adapter;
    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        serializer = mock(JsonAuditSerializer.class);
        properties = mock(AuditProperties.class);
        adapter = new ServiceBusPublisherAdapter(serializer, properties);
        auditoria = new Auditoria(
                "SMP", "trace-1", null, null,
                RegionEnum.ESTE_EEUU_2,
                StatusCodeEnum.STATUS_CORRECTO,
                TransactionCodeEnum.CONSULTA_CLIENTE,
                "device-1", "IPH"
        );
        when(properties.obtenerRegion(RegionEnum.ESTE_EEUU_2)).thenReturn("este2");
        when(properties.obtenerCodigo(TransactionCodeEnum.CONSULTA_CLIENTE)).thenReturn("002");
        when(serializer.serializar(auditoria, "este2", "002")).thenReturn("{\"ok\":true}");
    }

    @Test
    void noDebeEjecutarSerializacionAntesDeLaSuscripcion() {
        var publicacion = adapter.publicar(auditoria);

        verify(serializer, never()).serializar(auditoria, "este2", "002");

        StepVerifier.create(publicacion).verifyComplete();
        verify(serializer).serializar(auditoria, "este2", "002");
    }

    @Test
    void debeCompletarSimulacionCuandoServiceBusEstaHabilitado() {
        when(properties.isServiceBusEnabled()).thenReturn(true);

        StepVerifier.create(adapter.publicar(auditoria))
                .verifyComplete();
    }

    @Test
    void debePropagarErrorDeSerializacion() {
        RuntimeException fallo = new RuntimeException("json inválido");
        when(serializer.serializar(auditoria, "este2", "002")).thenThrow(fallo);

        StepVerifier.create(adapter.publicar(auditoria))
                .expectErrorMatches(error -> error == fallo)
                .verify();
    }
}
