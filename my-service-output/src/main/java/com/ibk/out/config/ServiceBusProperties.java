package com.ibk.out.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.servicebus")
@Getter
@Setter
public class ServiceBusProperties {
    private String connectionString;
    private String customEndpointAddress;
    private String queueName;
}
