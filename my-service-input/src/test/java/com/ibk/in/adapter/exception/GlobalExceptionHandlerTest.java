package com.ibk.in.adapter.exception;

import com.ibk.core.exception.RecursoNoEncontradoException;
import com.ibk.in.adapter.dto.ApiErrorResponse;
import com.ibk.in.adapter.dto.ClienteRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void debeTraducirRecursoNoEncontradoA404() {
        var response = handler.handleRecursoNoEncontrado(
                new RecursoNoEncontradoException("Cliente ausente")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiErrorResponse body = (ApiErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Recurso no encontrado", body.errors());
        assertEquals("Cliente ausente", body.message());
    }

    @Test
    void debeTraducirErroresDeValidacionA400() throws NoSuchMethodException {
        ClienteRequest target = new ClienteRequest("", "Pérez", "Gómez", true);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "cliente");
        bindingResult.addError(new FieldError(
                "cliente", "nombre", "El nombre no puede estar vacío"
        ));
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod(
                "metodoConRequest", ClienteRequest.class
        );
        WebExchangeBindException exception = new WebExchangeBindException(
                new MethodParameter(method, 0), bindingResult
        );

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiErrorResponse body = (ApiErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals(Map.of("nombre", "El nombre no puede estar vacío"), body.errors());
        assertEquals("La solicitud contiene campos inválidos", body.message());
    }

    @SuppressWarnings("unused")
    private void metodoConRequest(ClienteRequest request) {
    }
}
