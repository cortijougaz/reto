package com.ibk.out.config;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AuditProperties {
    private boolean serviceBusEnabled;
    private Map<String, String> transactionCodes;
    private Map<String, String> regions;

    public String obtenerCodigo(TransactionCodeEnum transaction) {
        if (transactionCodes == null || transaction == null) {
            return "000";
        }

        return transactionCodes.getOrDefault(transaction.name(), "000");
    }

    public String obtenerRegion(RegionEnum region) {
        if (regions == null || region == null) {
            return "este2";
        }

        return regions.getOrDefault(region.name(), "este2");
    }
}
