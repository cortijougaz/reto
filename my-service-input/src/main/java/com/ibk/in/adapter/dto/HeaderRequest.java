package com.ibk.in.adapter.dto;

public record HeaderRequest(
        String consumerId,
        String traceparent,
        String deviceType,
        String deviceId
) {
}
