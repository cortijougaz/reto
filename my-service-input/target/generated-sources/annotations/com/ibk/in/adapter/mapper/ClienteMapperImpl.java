package com.ibk.in.adapter.mapper;

import com.ibk.core.model.Cliente;
import com.ibk.core.model.RegisterUserCommand;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.ClienteResponse;
import com.ibk.in.adapter.dto.HeaderRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-13T16:28:52-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public RegisterUserCommand toRegisterUserCommand(HeaderRequest headers) {
        if ( headers == null ) {
            return null;
        }

        String consumerId = null;
        String traceparent = null;
        String deviceType = null;
        String deviceId = null;

        consumerId = headers.consumerId();
        traceparent = headers.traceparent();
        deviceType = headers.deviceType();
        deviceId = headers.deviceId();

        RegisterUserCommand registerUserCommand = new RegisterUserCommand( consumerId, traceparent, deviceType, deviceId );

        return registerUserCommand;
    }

    @Override
    public Cliente toDomain(ClienteRequest request) {
        if ( request == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setNombre( request.nombre() );
        cliente.setApellidoPaterno( request.apellidoPaterno() );
        cliente.setApellidoMaterno( request.apellidoMaterno() );
        if ( request.estado() != null ) {
            cliente.setEstado( request.estado() );
        }

        return cliente;
    }

    @Override
    public ClienteResponse toResponse(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        String id = null;

        id = cliente.getId();

        String nombreCompleto = nombreCompleto(cliente);

        ClienteResponse clienteResponse = new ClienteResponse( id, nombreCompleto );

        return clienteResponse;
    }
}
