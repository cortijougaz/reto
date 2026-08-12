package com.ibk.app.config;

import com.ibk.core.util.UseCaseService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = {
                "com.ibk.core.business"
        },
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = UseCaseService.class
        ),
        useDefaultFilters = false
)
public class UseCaseServiceConfig {
}
