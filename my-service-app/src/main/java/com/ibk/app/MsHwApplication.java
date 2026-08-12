package com.ibk.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = "com.ibk")
@SpringBootApplication(scanBasePackages = "com.ibk")
public class MsHwApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsHwApplication.class, args);
    }

}
