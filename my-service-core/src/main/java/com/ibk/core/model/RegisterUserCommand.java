package com.ibk.core.model;

public record RegisterUserCommand(
        String consumerId,
        String traceparent,
        String deviceType,
        String deviceId
) {
}
