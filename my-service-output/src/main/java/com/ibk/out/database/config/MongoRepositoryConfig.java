package com.ibk.out.database.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.ibk.out.database.repository")
public class MongoRepositoryConfig {
}
