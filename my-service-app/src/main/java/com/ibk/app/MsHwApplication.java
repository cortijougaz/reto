package com.ibk.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MsHwApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsHwApplication.class, args);
    }

}
