package mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable;

import lombok.Data;
import mx.com.ga.cosmonaut.orquestador.entity.SumaDeduciones;
import mx.com.ga.cosmonaut.orquestador.entity.SumaPercepcion;

import java.util.List;

@Data
public class PolizaContable {

    private String polizaContable;
    private String nombreReporte;
    private String fechaInicio;
    private String fechaFin;
    private String nombreNomina;
    private String nombreCliente;
    private List<SumaPercepcion> agrupadorPercepciones;
    private List<SumaDeduciones> agrupadorDeducciones;

}
