package com.ibk.in.adapter.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        Object errors,
        String message
) {
}
