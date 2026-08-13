package com.ibk.core.enums;

public enum StatusCodeEnum {
    STATUS_CORRECTO("0000"),
    STATUS_DESCONOCIDO("9999");

    private final String codigo;

    StatusCodeEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
