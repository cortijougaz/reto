package com.ibk.in.adapter.dto;

import java.util.UUID;

public record ClienteResponse(
        UUID id, String nombreCompleto
) {
}
