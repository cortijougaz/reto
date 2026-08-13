package com.ibk.in.adapter.controller;

import com.ibk.core.model.Cliente;
import com.ibk.core.port.in.usecase.ActualizarClienteInputPort;
import com.ibk.core.port.in.usecase.CrearClienteInputPort;
import com.ibk.core.port.in.usecase.EliminarClienteInputPort;
import com.ibk.core.port.in.usecase.ListarClienteInputPort;
import com.ibk.in.adapter.dto.ClienteRequest;
import com.ibk.in.adapter.dto.HeaderRequest;
import com.ibk.in.adapter.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteControllerTest {

    private final HeaderRequest headers = new HeaderRequest("SMP", "trace", "IPH", "device-1");
    private final ClienteRequest request = new ClienteRequest("Juan", "Pérez", "Gómez", true);
    private final Cliente cliente = new Cliente(
            "id-1", "Juan", "Pérez", "Gómez",
            LocalDateTime.of(2026, 8, 13, 16, 30), true
    );

    private ActualizarClienteInputPort actualizarPort;
    private CrearClienteInputPort crearPort;
    private EliminarClienteInputPort eliminarPort;
    private ListarClienteInputPort listarPort;
    private ClienteController controller;

    @BeforeEach
    void setUp() {
        actualizarPort = mock(ActualizarClienteInputPort.class);
        crearPort = mock(CrearClienteInputPort.class);
        eliminarPort = mock(EliminarClienteInputPort.class);
        listarPort = mock(ListarClienteInputPort.class);
        ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);
        controller = new ClienteController(actualizarPort, crearPort, eliminarPort, listarPort, mapper);
    }

    @Test
    void debeResponder200AlActualizar() {
        when(actualizarPort.actualizarCliente(any(), eq("id-1"), any())).thenReturn(Mono.just(cliente));

        StepVerifier.create(controller.actualizarCliente(headers, "id-1", request))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("id-1", response.getBody().id());
                })
                .verifyComplete();
    }

    @Test
    void debeResponder200AlCrear() {
        when(crearPort.crearCliente(any(), any())).thenReturn(Mono.just(cliente));

        StepVerifier.create(controller.crearCliente(headers, request))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("Juan Pérez Gómez", response.getBody().nombreCompleto());
                })
                .verifyComplete();
    }

    @Test
    void debeResponderClienteBuscado() {
        when(listarPort.listarPorIdCliente(any(), eq("id-1"))).thenReturn(Mono.just(cliente));

        StepVerifier.create(controller.buscarPorIdCliente(headers, "id-1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("id-1", response.getBody().id());
                })
                .verifyComplete();
    }

    @Test
    void debeResponderFlujoDeClientes() {
        Cliente segundo = new Cliente(
                "id-2", "Ana", "López", "Díaz",
                LocalDateTime.of(2026, 8, 13, 17, 0), true
        );
        when(listarPort.listarCliente(any())).thenReturn(Flux.just(cliente, segundo));

        StepVerifier.create(controller.buscarCliente(headers))
                .assertNext(response -> assertEquals("id-1", response.id()))
                .assertNext(response -> assertEquals("id-2", response.id()))
                .verifyComplete();
    }

    @Test
    void debeResponder204AlEliminar() {
        when(eliminarPort.eliminarPorIdCliente(any(), eq("id-1"))).thenReturn(Mono.empty());

        StepVerifier.create(controller.eliminarPorIdCliente(headers, "id-1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                    assertNull(response.getBody());
                })
                .verifyComplete();
    }
}
