package mx.com.ga.cosmonaut.reportes.dto.respuesta.reportenomina;

import lombok.Data;
import mx.com.ga.cosmonaut.orquestador.entity.Deducciones;
import mx.com.ga.cosmonaut.orquestador.entity.Percepciones;
import mx.com.ga.cosmonaut.orquestador.entity.Provisiones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.getFechaConversion;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.redondeoCantidad;

@Data
public class ReporteNomina {

    private String nombre;
    private String nombre2;
    private String apellidoPat;
    private String apellidoMat;
    private String rfc;
    private String curp;
    private String nss;
    private String numEmpleado;
    private String sbc;
    private String diasLaborados;
    private String diasAusencia;
    private String diasIncapacidad;
    private String totalPercepciones;
    private String totalDeducciones;
    private String totalNeto;
    private String totalProvisiones;
    private String nominaXperiodoId;
    private String fechaContrato;
    private String personaId;
    private String centrocClienteId;
    private String areaId;
    private String puesto;
    private String fechaPagoTimbrado;
    private String metodoPago;
    private String moneda;
    private String banco;
    private String fechaAntiguedad;
    private String fechaIngreso;
    private String clabe;
    private String numCuenta;
    private String cuentaBancareaia;
    private String provisionImssPatronal;
    private String provisionIsn;
    private String provisionVacaciones;
    private String provisionAguinaldo;
    private String provisionPrimaVacacional;
    private String salarioDiario;
    private String sueldoBrutoMensual;
    private String descripcionAreaea;
    private String descripcionSede;
    private String entidadFederativaColaborador;
    private String direccion;
    private List<Percepciones> agrupadorPercepciones;
    private List<Deducciones> agrupadorDeducciones;
    private List<Provisiones> agrupadorProvisiones;
    private String clienteNombre;
    private String nominaFechaInicio;
    private String nominaFechaFin;
    private String nombreNomina;


    public String getInfo(Integer valor) throws ParseException {
        switch (valor) {
            case 0: return nombre != null ? getNombre() : "";
            case 1: return apellidoPat != null ? getApellidoPat() : "";
            case 2: return apellidoMat != null ? getApellidoMat() : "";
            case 3: return rfc != null ? getRfc() : "";
            case 4: return curp != null ? getCurp() : "";
            case 5: return nss != null ? getNss() : "";
            case 6: return numEmpleado != null ? getNumEmpleado() : "";
            case 7: return sbc != null ? redondeoCantidad(getSbc(), 2) : "";
            case 8: return diasLaborados != null ? getDiasLaborados() : "";
            case 9: return diasAusencia != null ? getDiasAusencia() : "";
            case 10: return diasIncapacidad != null ? getDiasIncapacidad() : "";
            case 11: return totalPercepciones != null ? getTotalPercepciones() : "";
            case 12: return totalDeducciones != null ? getTotalDeducciones() : "";
            case 13: return totalNeto != null ? getTotalNeto() : "";
            case 14: return totalProvisiones != null ? getTotalProvisiones() : "";
            case 15: return fechaContrato != null ? getFechaConversion(getFechaContrato()) : "";
            case 16: return puesto != null ? getPuesto() : "";
            case 17: return fechaPagoTimbrado != null ? getFechaConversion(getFechaPagoTimbrado()) : "";
            case 18: return metodoPago != null ? getMetodoPago() : "";
            case 19: return moneda != null ? getMoneda() : "";
            case 20: return banco != null ? getBanco() : "";
            case 21: return fechaAntiguedad != null ? getFechaConversion(getFechaAntiguedad()) : "";
            case 22: return fechaIngreso != null ? getFechaConversion(getFechaIngreso()) : "";
            case 23: return clabe != null ? getClabe() : "";
            case 24: return numCuenta != null ? getNumCuenta() : "";
            case 25: return cuentaBancareaia != null ? getCuentaBancareaia() : "";
            case 26: return provisionImssPatronal != null ? getProvisionImssPatronal() : "";
            case 27: return provisionIsn != null ? getProvisionIsn() : "";
            case 28: return provisionVacaciones != null ? getProvisionVacaciones() : "";
            case 29: return provisionAguinaldo != null ? getProvisionAguinaldo() : "";
            case 30: return provisionPrimaVacacional != null ? getProvisionPrimaVacacional() : "";
            case 31: return salarioDiario != null ? getSalarioDiario() : "";
            case 32: return sueldoBrutoMensual != null ? getSueldoBrutoMensual() : "";
            case 33: return descripcionAreaea != null ? getDescripcionAreaea() : "";
            case 34: return descripcionSede != null ? getDescripcionSede() : "";
            case 35: return entidadFederativaColaborador != null ? getEntidadFederativaColaborador() : "";
            case 36: return direccion != null ? getDireccion() : "";
            default: return "";
        }
    }
}
