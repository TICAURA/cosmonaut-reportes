package mx.com.ga.cosmonaut.reportes.dto.respuesta.detallenominahistorica;

import lombok.Data;

import java.util.List;

@Data
public class CalculoXempleado {
    private String empleado;
    private String descripcionPuesto;
    private String deboSerSbc;
    private List<Provision> provision;
    private List<Percepcion> percepcion;
    private List<Deduccion> deduccion;
    private String netoApagar;
    private String provisiones;
    private String percepciones;
    private String deducciones;
}
