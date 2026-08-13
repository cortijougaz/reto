package com.ibk.out.database.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteEntityMapperTest {

    private final ClienteEntityMapper mapper = Mappers.getMapper(ClienteEntityMapper.class);

    @Test
    void debeMapearDominioAEntidadSinPerderFechaHora() {
        LocalDateTime fecha = LocalDateTime.of(2026, 8, 13, 16, 30, 45);
        Cliente cliente = new Cliente("id-1", "Juan", "Pérez", "Gómez", fecha, true);

        ClienteEntity entity = mapper.toEntity(cliente);

        assertEquals(cliente.getId(), entity.getId());
        assertEquals(cliente.getNombre(), entity.getNombre());
        assertEquals(fecha, entity.getFechaCreacion());
        assertEquals(cliente.isEstado(), entity.isEstado());
    }

    @Test
    void debeMapearEntidadADominio() {
        LocalDateTime fecha = LocalDateTime.of(2026, 8, 13, 16, 30, 45);
        ClienteEntity entity = ClienteEntity.builder()
                .id("id-1")
                .nombre("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .fechaCreacion(fecha)
                .estado(true)
                .build();

        Cliente cliente = mapper.toDomain(entity);

        assertEquals(entity.getId(), cliente.getId());
        assertEquals(entity.getApellidoPaterno(), cliente.getApellidoPaterno());
        assertEquals(fecha, cliente.getFechaCreacion());
        assertEquals(entity.isEstado(), cliente.isEstado());
    }
}
