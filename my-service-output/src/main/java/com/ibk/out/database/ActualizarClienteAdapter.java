package com.ibk.out.database;

import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.out.database.mapper.ClienteEntityMapper;
import com.ibk.out.database.repository.ClienteEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteAdapter implements ActualizarClienteOutputPort {

    private final ClienteEntityRepository repository;
    private final ClienteEntityMapper clienteEntityMapper;

    @Override
    public Mono<Cliente> actualizarCliente(UUID id, Cliente cliente) {
        var entity = clienteEntityMapper.toEntity(id, cliente);

        return repository.save(entity)
                .map(clienteEntityMapper::toDomain);
    }
}
