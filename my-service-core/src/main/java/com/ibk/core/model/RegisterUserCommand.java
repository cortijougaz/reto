package com.ibk.core.model;

public record RegisterUserCommand(
        String consumerId,
        String traceparent,
        String deviceType,
        String deviceId
) {
    public String traceId() {
        if (traceparent == null || traceparent.isBlank()) {
            return "";
        }

        String[] partes = traceparent.split("-");

        if (partes.length >= 2 && !partes[1].isBlank()) {
            return partes[1];
        }

        return traceparent;
    }
}
