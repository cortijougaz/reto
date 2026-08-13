package com.ibk.out.database;

import com.ibk.core.model.Cliente;
import com.ibk.out.database.entity.ClienteEntity;
import com.ibk.out.database.mapper.ClienteEntityMapper;
import com.ibk.out.database.repository.ClienteEntityRepository;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientePersistenceAdapterTest {

    private ClienteEntityRepository repository;
    private ClienteEntityMapper mapper;
    private ReactiveMongoTemplate mongoTemplate;
    private ClientePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ClienteEntityRepository.class);
        mapper = mock(ClienteEntityMapper.class);
        mongoTemplate = mock(ReactiveMongoTemplate.class);
        adapter = new ClientePersistenceAdapter(repository, mapper, mongoTemplate);
    }

    @Test
    void debeCrearMapeandoDominioAEntidadYRegresandoDominio() {
        Cliente entrada = cliente("id-1");
        ClienteEntity entity = entity("id-1");
        when(mapper.toEntity(entrada)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(entrada);

        StepVerifier.create(adapter.crear(entrada))
                .expectNext(entrada)
                .verifyComplete();

        verify(repository).save(entity);
    }

    @Test
    void debeBuscarPorIdYMapearEntidad() {
        ClienteEntity entity = entity("id-1");
        Cliente cliente = cliente("id-1");
        when(repository.findById("id-1")).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(cliente);

        StepVerifier.create(adapter.listarPorId("id-1"))
                .expectNext(cliente)
                .verifyComplete();
    }

    @Test
    void debeListarTodasLasEntidadesMapeadas() {
        ClienteEntity uno = entity("1");
        ClienteEntity dos = entity("2");
        Cliente clienteUno = cliente("1");
        Cliente clienteDos = cliente("2");
        when(repository.findAll()).thenReturn(Flux.just(uno, dos));
        when(mapper.toDomain(uno)).thenReturn(clienteUno);
        when(mapper.toDomain(dos)).thenReturn(clienteDos);

        StepVerifier.create(adapter.listar())
                .expectNext(clienteUno, clienteDos)
                .verifyComplete();
    }

    @Test
    void debeActualizarSoloCamposEditablesYDevolverDocumentoNuevo() {
        Cliente cambios = cliente("ignorado");
        ClienteEntity actualizada = entity("id-1");
        Cliente resultado = cliente("id-1");
        when(mongoTemplate.findAndModify(
                any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ClienteEntity.class)
        )).thenReturn(Mono.just(actualizada));
        when(mapper.toDomain(actualizada)).thenReturn(resultado);

        StepVerifier.create(adapter.actualizarCliente("id-1", cambios))
                .expectNext(resultado)
                .verifyComplete();

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        ArgumentCaptor<FindAndModifyOptions> optionsCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);
        verify(mongoTemplate).findAndModify(
                queryCaptor.capture(), updateCaptor.capture(), optionsCaptor.capture(),
                eq(ClienteEntity.class)
        );
        assertEquals("id-1", queryCaptor.getValue().getQueryObject().get("id"));
        Document campos = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
        assertEquals(cambios.getNombre(), campos.get("nombre"));
        assertEquals(cambios.isEstado(), campos.get("estado"));
        assertFalse(campos.containsKey("fechaCreacion"));
        assertTrue(optionsCaptor.getValue().isReturnNew());
        assertFalse(optionsCaptor.getValue().isUpsert());
    }

    @Test
    void debeReportarSiMongoEliminoElDocumento() {
        when(mongoTemplate.remove(any(Query.class), eq(ClienteEntity.class)))
                .thenReturn(Mono.just(DeleteResult.acknowledged(1)));

        StepVerifier.create(adapter.eliminarPorId("id-1"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void debeReportarFalseCuandoMongoNoEliminoDocumentos() {
        when(mongoTemplate.remove(any(Query.class), eq(ClienteEntity.class)))
                .thenReturn(Mono.just(DeleteResult.acknowledged(0)));

        StepVerifier.create(adapter.eliminarPorId("ausente"))
                .expectNext(false)
                .verifyComplete();
    }

    private Cliente cliente(String id) {
        return new Cliente(
                id, "Juan", "Pérez", "Gómez",
                LocalDateTime.of(2026, 8, 13, 16, 30), true
        );
    }

    private ClienteEntity entity(String id) {
        return ClienteEntity.builder()
                .id(id)
                .nombre("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .fechaCreacion(LocalDateTime.of(2026, 8, 13, 16, 30))
                .estado(true)
                .build();
    }
}
