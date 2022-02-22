package mx.com.ga.cosmonaut.reportes.dto.respuesta.reporteraya;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class DetalleXempleado {

    private String id;
    private String primerApellido;
    private String segundoApellido;
    private String nombres;
    private String puesto;
    private String fechaAntiguedad;
    private String diasPagados;
    private String rfc;
    private String sueldoDiario;
    private String totalHorasTrabajadas;
    private String area;
    private String nss;
    private String sbc;
    private String horasDia;
    private String curp;
    private String cotizacion;
    private String horasExtra;
    private List<Percepciones> percepciones;
    private List<Deducciones> deducciones;
    private String totalPercepciones;
    private String totalDeducciones;
    private String netoApagar;
}
