package com.ibk.in.adapter.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.ClienteResponse;
import com.ibk.in.adapter.dto.HeaderRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClienteMapperTest {

    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    @Test
    void debeMapearRequestADominioSinCamposGestionadosPorLaAplicacion() {
        ClienteRequest request = new ClienteRequest("Juan", "Pérez", "Gómez", false);

        Cliente cliente = mapper.toDomain(request);

        assertNull(cliente.getId());
        assertNull(cliente.getFechaCreacion());
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Pérez", cliente.getApellidoPaterno());
        assertEquals("Gómez", cliente.getApellidoMaterno());
        assertFalse(cliente.isEstado());
    }

    @Test
    void debeMapearDominioAResponseConNombreCompleto() {
        Cliente cliente = new Cliente(
                "id-1", "Juan", "Pérez", "Gómez",
                LocalDateTime.of(2026, 8, 13, 16, 30), true
        );

        ClienteResponse response = mapper.toResponse(cliente);

        assertEquals("id-1", response.id());
        assertEquals("Juan Pérez Gómez", response.nombreCompleto());
    }

    @Test
    void debeMapearHeadersAComando() {
        HeaderRequest headers = new HeaderRequest("SMP", "trace", "IPH", "device-1");

        var command = mapper.toRegisterUserCommand(headers);

        assertEquals(headers.consumerId(), command.consumerId());
        assertEquals(headers.traceparent(), command.traceparent());
        assertEquals(headers.deviceType(), command.deviceType());
        assertEquals(headers.deviceId(), command.deviceId());
    }
}
