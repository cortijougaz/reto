package com.ibk.out.database.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection="clientes")
public class ClienteEntity {

    @Id
    private String id;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private LocalDateTime fechaCreacion;

    private boolean estado;
}
