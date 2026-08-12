package com.ibk.core.model;

public record RegisterUserCommand(
        String name,
        String email,
        String consumerId,
        String traceparent
) {
}
