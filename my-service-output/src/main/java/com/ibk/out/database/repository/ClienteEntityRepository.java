package com.ibk.out.database.repository;

import com.ibk.out.database.entity.ClienteEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ClienteEntityRepository extends ReactiveMongoRepository<ClienteEntity, String> {
}
