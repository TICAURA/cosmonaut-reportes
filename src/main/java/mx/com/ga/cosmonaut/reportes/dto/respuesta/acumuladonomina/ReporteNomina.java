package mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladonomina;

import lombok.Data;

import java.text.ParseException;
import java.util.List;

import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.formatoFecha;

@Data
public class ReporteNomina {
    private String numEmpleado;
    private String nombre;
    private String apellidoPat;
    private List<AgrupadorPercepciones> agrupadorPercepciones;
    private String totalPercepciones;
    private List<AgrupadorDeducciones> agrupadorDeducciones;
    private String totalDeducciones;
    private String totalNeto;
    private String fechaContratoNogrupo;
    private String personaId;

    public String getInfo(Integer parametro) throws ParseException {
        switch (parametro) {
            case 0: return numEmpleado != null ? getNumEmpleado() : "";
            case 1: return nombre != null ? getNombre() : "";
            case 2: return apellidoPat != null ? getApellidoPat() : "";
            case 3: return totalPercepciones != null ? getTotalPercepciones() : "";
            case 4: return totalDeducciones != null ? getTotalDeducciones() : "";
            case 5: return totalNeto != null ? getTotalNeto() : "";
            case 6: return fechaContratoNogrupo != null ? formatoFecha(getFechaContratoNogrupo()) : "";
            case 7: return personaId != null ? getPersonaId() : "";
            default: return "";
        }
    }
}
