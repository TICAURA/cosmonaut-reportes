package mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable;

import lombok.Data;

@Data
public class AgrupadorPercepciones {

    private String cuentaContable;
    private String concepto;
    private Float cargo;
    private Float abono;
}
