package com.ibk.out.database.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteEntityMapper {

    ClienteEntity toEntity(Cliente cliente);

    Cliente toDomain(ClienteEntity entity);
}
