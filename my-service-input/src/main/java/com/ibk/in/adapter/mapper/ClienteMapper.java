package com.ibk.in.adapter.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.ClienteResponse;
import com.ibk.in.adapter.dto.HeaderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    RegisterUserCommand toRegisterUserCommand(HeaderRequest headers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Cliente toDomain(ClienteRequest request);

    @Mapping(target = "nombreCompleto", expression = "java(nombreCompleto(cliente))")
    ClienteResponse toResponse(Cliente cliente);

    default String nombreCompleto(Cliente cliente) {
        return String.join(" ",
                cliente.getNombre(),
                cliente.getApellidoPaterno(),
                cliente.getApellidoMaterno()
        );
    }
}
