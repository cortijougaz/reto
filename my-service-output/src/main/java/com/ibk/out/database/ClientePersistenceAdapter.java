package com.ibk.out.database;

import com.ibk.core.model.Cliente;
import com.ibk.core.port.out.persistence.ActualizarClienteOutputPort;
import com.ibk.core.port.out.persistence.BuscarClienteOutputPort;
import com.ibk.core.port.out.persistence.CrearClienteOutputPort;
import com.ibk.core.port.out.persistence.EliminarClienteOutputPort;
import com.ibk.core.port.out.persistence.ListarClientesOutputPort;
import com.ibk.out.database.entity.ClienteEntity;
import com.ibk.out.database.mapper.ClienteEntityMapper;
import com.ibk.out.database.repository.ClienteEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements CrearClienteOutputPort,
        BuscarClienteOutputPort,
        ListarClientesOutputPort,
        ActualizarClienteOutputPort,
        EliminarClienteOutputPort {

    private final ClienteEntityRepository repository;
    private final ClienteEntityMapper clienteEntityMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Cliente> actualizarCliente(String id, Cliente cliente) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = new Update()
                .set("nombre", cliente.getNombre())
                .set("apellidoPaterno", cliente.getApellidoPaterno())
                .set("apellidoMaterno", cliente.getApellidoMaterno())
                .set("estado", cliente.isEstado());
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(false);

        return reactiveMongoTemplate
                .findAndModify(query, update, options, ClienteEntity.class)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public Mono<Cliente> listarPorId(String id) {
        return repository.findById(id)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public Mono<Cliente> crear(Cliente cliente) {
        ClienteEntity entity = clienteEntityMapper.toEntity(cliente);

        return repository.save(entity)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public Mono<Boolean> eliminarPorId(String id) {
        Query query = Query.query(Criteria.where("id").is(id));

        return reactiveMongoTemplate
                .remove(query, ClienteEntity.class)
                .map(result -> result.getDeletedCount() > 0);
    }

    @Override
    public Flux<Cliente> listar() {
        return repository.findAll()
                .map(clienteEntityMapper::toDomain);
    }
}
