package mx.com.ga.cosmonaut.reportes.dto.respuesta.promediovariables;

import lombok.Data;

import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.redondeoCantidad;

@Data
public class PromedioVariables {

    private String periodo;
    private String fechaInicio;
    private String fechaFin;
    private String totalEmpleados;
    private String numEmpleado;
    private String nombre;
    private String apellidoPat;
    private String apellidoMat;
    private String sueldoBrutoMensual;
    private String salarioDiario;
    private String factorIntegracion;
    private String diasBimestre;
    private String diasLaboradosBimestre;
    private String ingresosVariables;
    private String promedioVariable;
    private String sbcAnterior;
    private String sbcActual;
    private String diferencia;
    private String variabilidadId;
    private String razonSocial;
    private String nombreEmpresa;

    public String getProperty(Integer c) {
        switch (c) {
            case 0 : return numEmpleado != null ? getNumEmpleado() : "";
            case 1 : return nombre != null ? getNombre() + " " + getApellidoPat() + " " + getApellidoMat() : "";
            case 2 : return sueldoBrutoMensual != null ? getSueldoBrutoMensual() : "";
            case 3 : return salarioDiario != null ? redondeoCantidad(getSalarioDiario(), 2) : "";
            case 4 : return factorIntegracion != null ? getFactorIntegracion() : "";
            case 5 : return diasBimestre != null ? getDiasBimestre() : "";
            case 6 : return diasLaboradosBimestre != null ? getDiasLaboradosBimestre() : "";
            case 7 : return ingresosVariables != null ? getIngresosVariables() : "";
            case 8 : return promedioVariable != null ? getPromedioVariable() : "";
            case 9 : return sbcAnterior != null ? redondeoCantidad(getSbcAnterior(), 2) : "";
            case 10 : return sbcActual != null ? redondeoCantidad(getSbcActual(), 2) : "";
            case 11 : return diferencia != null ? redondeoCantidad(getDiferencia(), 2) : "";
            default: return "";
        }
    }
}
