package mx.com.ga.cosmonaut.reportes.model;

import lombok.Data;

@Data
public class ComprobanteFiscalModel {

    private Integer nominaPeriodoId;
    private String idEmpleado;
    private Integer clienteId;
    private boolean esZip;
}
