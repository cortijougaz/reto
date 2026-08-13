package com.ibk.out.publisher;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.ibk.core.model.Auditoria;
import com.ibk.core.port.out.publisher.AuditoriaPublisherOutputPort;
import com.ibk.out.config.AuditProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceBusPublisherAdapter implements AuditoriaPublisherOutputPort {

    private final JsonAuditSerializer serializer;
    private final AuditProperties properties;

    @Override
    public Mono<Void> publicar(Auditoria auditoria) {
        return Mono.defer(() -> {
            String json = serializer.serializar(
                    auditoria,
                    properties.obtenerRegion(auditoria.region()),
                    properties.obtenerCodigo(auditoria.transaction())
            );

            if (!properties.isServiceBusEnabled()) {
                return Mono.empty();
            }

            return enviarAServiceBus(json, auditoria.traceId());
        });
    }

    private Mono<Void> enviarAServiceBus(
            String json,
            String traceId
    ) {
        return Mono.fromRunnable(() -> {
            ServiceBusMessage message = new ServiceBusMessage(json);
            message.setContentType("application/json");
            message.getApplicationProperties().put("traceId", traceId);

            log.info("[LOCAL] Simulación de envío a Azure Service Bus.");
            log.info("[MESSAGE-ID]: {}", message.getMessageId());
            log.info("[TRACE-ID]: {}", traceId);
            log.info("[PAYLOAD-JSON]: {}", json);
        });
    }
}
