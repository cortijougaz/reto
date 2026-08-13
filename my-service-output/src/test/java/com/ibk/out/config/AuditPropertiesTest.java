package com.ibk.out.config;

import com.ibk.core.enums.RegionEnum;
import com.ibk.core.enums.TransactionCodeEnum;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditPropertiesTest {

    @Test
    void debeResolverCodigosYRegionesConfigurados() {
        AuditProperties properties = new AuditProperties();
        properties.setTransactionCodes(Map.of(TransactionCodeEnum.REGISTRO_CLIENTE.name(), "102"));
        properties.setRegions(Map.of(RegionEnum.ESTE_EEUU_2.name(), "este2-configurado"));

        assertEquals("102", properties.obtenerCodigo(TransactionCodeEnum.REGISTRO_CLIENTE));
        assertEquals("este2-configurado", properties.obtenerRegion(RegionEnum.ESTE_EEUU_2));
    }

    @Test
    void debeAplicarValoresPorDefectoAnteConfiguracionAusente() {
        AuditProperties properties = new AuditProperties();

        assertEquals("000", properties.obtenerCodigo(TransactionCodeEnum.CONSULTA_CLIENTE));
        assertEquals("000", properties.obtenerCodigo(null));
        assertEquals("este2", properties.obtenerRegion(RegionEnum.CENTRO_EEUU));
        assertEquals("este2", properties.obtenerRegion(null));
    }
}
