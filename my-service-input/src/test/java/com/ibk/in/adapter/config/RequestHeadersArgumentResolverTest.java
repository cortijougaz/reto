package com.ibk.in.adapter.config;

import com.ibk.in.adapter.dto.HeaderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RequestHeadersArgumentResolverTest {

    private final RequestHeadersArgumentResolver resolver = new RequestHeadersArgumentResolver();

    @Test
    void debeSoportarSoloHeaderRequest() throws NoSuchMethodException {
        Method method = RequestHeadersArgumentResolverTest.class.getDeclaredMethod(
                "metodoConParametros", HeaderRequest.class, String.class
        );
        MethodParameter soportado = new MethodParameter(method, 0);
        MethodParameter noSoportado = new MethodParameter(method, 1);

        assertTrue(resolver.supportsParameter(soportado));
        assertFalse(resolver.supportsParameter(noSoportado));
    }

    @Test
    void debeResolverCabecerasDelExchange() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/cliente/buscar")
                        .header("consumerId", "SMP")
                        .header("traceparent", "00-trace-parent-01")
                        .header("deviceType", "IPH")
                        .header("deviceId", "device-1")
                        .build()
        );

        StepVerifier.create(resolver.resolveArgument(
                        mock(MethodParameter.class),
                        mock(BindingContext.class),
                        exchange
                ))
                .assertNext(value -> {
                    HeaderRequest headers = (HeaderRequest) value;
                    assertEquals("SMP", headers.consumerId());
                    assertEquals("00-trace-parent-01", headers.traceparent());
                    assertEquals("IPH", headers.deviceType());
                    assertEquals("device-1", headers.deviceId());
                })
                .verifyComplete();
    }

    @SuppressWarnings("unused")
    private void metodoConParametros(HeaderRequest headers, String valor) {
    }
}
