package com.ibk.out.database.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T18:56:30-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClienteEntityMapperImpl implements ClienteEntityMapper {

    @Override
    public ClienteEntity toEntity(UUID id, Cliente cliente) {
        if ( id == null && cliente == null ) {
            return null;
        }

        ClienteEntity.ClienteEntityBuilder clienteEntity = ClienteEntity.builder();

        if ( cliente != null ) {
            clienteEntity.nombre( cliente.getNombre() );
            clienteEntity.apellidoPaterno( cliente.getApellidoPaterno() );
            clienteEntity.apellidoMaterno( cliente.getApellidoMaterno() );
            clienteEntity.fechaCreacion( cliente.getFechaCreacion() );
            clienteEntity.estado( cliente.isEstado() );
        }
        clienteEntity.id( id );

        return clienteEntity.build();
    }

    @Override
    public Cliente toDomain(ClienteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId( entity.getId() );
        cliente.setNombre( entity.getNombre() );
        cliente.setApellidoPaterno( entity.getApellidoPaterno() );
        cliente.setApellidoMaterno( entity.getApellidoMaterno() );
        cliente.setFechaCreacion( entity.getFechaCreacion() );
        cliente.setEstado( entity.isEstado() );

        return cliente;
    }
}
