package com.ibk.out.publisher;

import com.ibk.core.model.Auditoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class JsonAuditSerializer {

    private static final String TRACER_ROUTE = "application";

    private final ObjectMapper objectMapper;

    public String serializar(
            Auditoria auditoria,
            String region,
            String transactionCode
    ) {
        ObjectNode rootNode = objectMapper.createObjectNode();

        rootNode.put(
                "analyticsTraceSource",
                "%s-%s".formatted(
                        TRACER_ROUTE,
                        auditoria.consumerId()
                )
        );
        rootNode.put("applicationId", auditoria.consumerId());
        rootNode.put("consumerId", auditoria.consumerId());
        rootNode.put(
                "currentDate",
                OffsetDateTime.now()
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
        rootNode.put("region", region);
        rootNode.put(
                "statusCode",
                auditoria.status().getCodigo()
        );
        rootNode.put("timestamp", System.currentTimeMillis());
        rootNode.put("traceId", auditoria.traceId());
        rootNode.put(
                "inbound",
                convertirInbound(
                        auditoria.inbound(),
                        auditoria.deviceId(),
                        auditoria.deviceType()
                )
        );
        rootNode.put(
                "outbound",
                convertirObjeto(auditoria.outbound())
        );
        rootNode.put("transactionCode", transactionCode);

        return objectMapper.writeValueAsString(rootNode);
    }

    private String convertirInbound(
            Object inbound,
            String deviceId,
            String deviceType
    ) {
        ObjectNode inboundNode = objectMapper.createObjectNode();

        inboundNode.put("deviceId", deviceId);
        inboundNode.put("deviceType", deviceType);

        if (inbound != null) {
            ObjectNode payloadNode = objectMapper.valueToTree(inbound);
            inboundNode.setAll(payloadNode);
        }

        return objectMapper.writeValueAsString(inboundNode);
    }

    private String convertirObjeto(Object object) {
        if (object == null) {
            return "{}";
        }

        return objectMapper.writeValueAsString(object);
    }
}
