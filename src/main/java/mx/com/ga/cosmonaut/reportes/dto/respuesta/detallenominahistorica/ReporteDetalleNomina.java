package mx.com.ga.cosmonaut.reportes.dto.respuesta.detallenominahistorica;

import lombok.Data;

import java.util.List;

@Data
public class ReporteDetalleNomina {
    private String nombreNomina;
    private String razonSocial;
    private String clavePeriodo;
    private String fechaInicio;
    private String fechaFin;
    private List<CalculoXempleado> calculoXempleado;
}
