package com.ibk.out.database.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClienteEntityMapper {

    @Mapping(target = "id", source = "id")
    ClienteEntity toEntity(UUID id, Cliente cliente);

    Cliente toDomain(ClienteEntity entity);
}
