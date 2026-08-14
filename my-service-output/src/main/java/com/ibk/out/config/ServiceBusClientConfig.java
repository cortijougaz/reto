package com.ibk.out.config;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ServiceBusClientConfig {

    @Bean(destroyMethod = "close")
    public ServiceBusSenderAsyncClient serviceBusSenderAsyncClient(
            ServiceBusProperties properties
    ) {
        ServiceBusClientBuilder builder = new ServiceBusClientBuilder()
                .connectionString(required(
                        properties.getConnectionString(),
                        "azure.servicebus.connection-string"
                ))
                .transportType(AmqpTransportType.AMQP);

        if (StringUtils.hasText(properties.getCustomEndpointAddress())) {
            builder.customEndpointAddress(
                    properties.getCustomEndpointAddress()
            );
        }

        return builder.sender()
                .queueName(required(
                        properties.getQueueName(),
                        "azure.servicebus.queue-name"
                ))
                .buildAsyncClient();
    }

    private static String required(String value, String property) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(
                    "Falta la configuración obligatoria: " + property
            );
        }
        return value;
    }
}
