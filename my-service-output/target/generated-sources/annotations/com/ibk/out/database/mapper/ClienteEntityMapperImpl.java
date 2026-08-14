package com.ibk.out.database.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-14T13:23:34-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClienteEntityMapperImpl implements ClienteEntityMapper {

    @Override
    public ClienteEntity toEntity(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteEntity.ClienteEntityBuilder clienteEntity = ClienteEntity.builder();

        clienteEntity.id( cliente.getId() );
        clienteEntity.nombre( cliente.getNombre() );
        clienteEntity.apellidoPaterno( cliente.getApellidoPaterno() );
        clienteEntity.apellidoMaterno( cliente.getApellidoMaterno() );
        clienteEntity.fechaCreacion( cliente.getFechaCreacion() );
        clienteEntity.estado( cliente.isEstado() );

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
