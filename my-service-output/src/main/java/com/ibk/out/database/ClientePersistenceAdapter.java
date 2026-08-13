package com.ibk.out.database;

import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.port.out.persistence.CrearClienteOutputPort;
import com.ibk.core.port.out.persistence.EliminarClienteOutputPort;
import com.ibk.core.port.out.persistence.ListarClientesOutputPort;
import com.ibk.out.database.mapper.ClienteEntityMapper;
import com.ibk.out.database.repository.ClienteEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements CrearClienteOutputPort,
        BuscarClienteOutputPort,
        ListarClientesOutputPort,
        ActualizarClienteOutputPort,
        EliminarClienteOutputPort {

    private final ClienteEntityRepository repository;
    private final ClienteEntityMapper clienteEntityMapper;

    @Override
    public Mono<Cliente> actualizarCliente(UUID id, Cliente cliente) {
        var entity = clienteEntityMapper.toEntity(id, cliente);

        return repository.save(entity)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public Mono<Cliente> buscarPorId(UUID id) {
        return repository.findById(id)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public Mono<Cliente> crear(Cliente cliente) {
        return null;
    }

    @Override
    public Mono<Void> eliminarPorId(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Cliente> listar() {
        return repository.findAll()
                .map(clienteEntityMapper::toDomain);
    }
}
