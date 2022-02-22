package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ReciboXempleado {

    @SerializedName("primerApellido")
    private String primerApellido;
    @SerializedName("segundoApellido")
    private String segundoApellido;
    @SerializedName("nombres")
    private String nombres;
    @SerializedName("noEmp")
    private String noEmp;
    @SerializedName("rfc")
    private String rfc;
    @SerializedName("curp")
    private String curp;
    @SerializedName("nss")
    private String nss;
    @SerializedName("puesto")
    private String puesto;
    @SerializedName("diasTrabajados")
    private String diasTrabajados;
    @SerializedName("faltas")
    private String faltas;
    @SerializedName("diasIncap")
    private String diasIncp;
    @SerializedName("sbc")
    private String sbc;
    @SerializedName("metodoPago")
    private String metodoPago;
    @SerializedName("fecha")
    private String fecha;
    @SerializedName("netoApagar")
    private String netoApagar;
    @SerializedName("percepcion")
    private List<Percepcion> percepcion;
    @SerializedName("deduccion")
    private List<Deduccion> deduccion;
    @SerializedName("percepciones")
    private String percepciones;
    @SerializedName("deducciones")
    private String deducciones;
    @SerializedName("netoApagar_endinero")
    private String netoApagar_endinero;
    @SerializedName("netoApagar_enespecie")
    private String netoApagar_enespecie;


}
