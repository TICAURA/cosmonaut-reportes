package mx.com.ga.cosmonaut.reportes.dto.respuesta.reporteraya;

import lombok.Data;

import java.util.List;

@Data
public class ReporteNominaHistorica {

    private String razonSocial;
    private String domicilio;
    private String rfc;
    private String registroPatronalImss;
    private String nombreNomina;
    private String del;
    private String al;
    private String fechaHora;
    private List<DetalleXempleado> detalleXempleado;
    private String totalPercepciones;
    private String totalDeducciones;
    private String totalApagar;
}
