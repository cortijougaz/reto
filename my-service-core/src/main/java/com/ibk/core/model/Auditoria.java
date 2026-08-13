package com.ibk.core.model;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.StatusCodeEnum;
import com.ibk.core.enums.TransactionCodeEnum;

public record Auditoria(
        String consumerId,
        String traceId,
        Object inbound,
        Object outbound,
        RegionEnum region,
        StatusCodeEnum status,
        TransactionCodeEnum transaction,
        String deviceId,
        String deviceType
) {
}
