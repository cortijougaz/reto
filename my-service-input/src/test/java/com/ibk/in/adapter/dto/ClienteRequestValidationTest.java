package com.ibk.in.adapter.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteRequestValidationTest {

    private static jakarta.validation.ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void debeAceptarRequestValido() {
        ClienteRequest request = new ClienteRequest("Juan", "Pérez", "", true);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void debeRechazarCamposObligatoriosInvalidos() {
        ClienteRequest request = new ClienteRequest(" ", "", null, null);

        Set<String> campos = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("nombre", "apellidoPaterno", "apellidoMaterno", "estado"), campos);
    }
}
