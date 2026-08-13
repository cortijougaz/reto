package com.ibk.out.publisher;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import com.ibk.core.model.Auditoria;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonAuditSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonAuditSerializer serializer = new JsonAuditSerializer(objectMapper);

    @Test
    void debeConstruirTramaDeAuditoriaConInboundYOutbound() {
        Auditoria auditoria = new Auditoria(
                "SMP", "trace-1",
                Map.of("id", "1"), Map.of("resultado", "ok"),
                RegionEnum.ESTE_EEUU_2,
                StatusCodeEnum.STATUS_CORRECTO,
                TransactionCodeEnum.ACTUALIZACION_CLIENTE,
                "device-1", "IPH"
        );

        String json = serializer.serializar(auditoria, "este2", "202");
        JsonNode root = objectMapper.readTree(json);

        assertEquals("application-SMP", root.get("analyticsTraceSource").asString());
        assertEquals("SMP", root.get("applicationId").asString());
        assertEquals("este2", root.get("region").asString());
        assertEquals("0000", root.get("statusCode").asString());
        assertEquals("trace-1", root.get("traceId").asString());
        assertEquals("202", root.get("transactionCode").asString());
        assertTrue(root.get("timestamp").asLong() > 0);

        JsonNode inbound = objectMapper.readTree(root.get("inbound").asString());
        assertEquals("device-1", inbound.get("deviceId").asString());
        assertEquals("IPH", inbound.get("deviceType").asString());
        assertEquals("1", inbound.get("id").asString());

        JsonNode outbound = objectMapper.readTree(root.get("outbound").asString());
        assertEquals("ok", outbound.get("resultado").asString());
    }

    @Test
    void debeSerializarObjetosAusentesComoJsonVacio() {
        Auditoria auditoria = new Auditoria(
                "SMP", "trace-1", null, null,
                RegionEnum.ESTE_EEUU_2,
                StatusCodeEnum.STATUS_DESCONOCIDO,
                TransactionCodeEnum.CONSULTA_CLIENTE,
                "device-1", "IPH"
        );

        JsonNode root = objectMapper.readTree(serializer.serializar(auditoria, "este2", "002"));
        JsonNode inbound = objectMapper.readTree(root.get("inbound").asString());

        assertEquals("device-1", inbound.get("deviceId").asString());
        assertEquals("IPH", inbound.get("deviceType").asString());
        assertEquals("{}", root.get("outbound").asString());
    }
}
