package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ReciboNomina {

    @SerializedName("nombreNomina")
    private String nombreNomina;
    @SerializedName("razonSocial")
    private String razonSocial;
    @SerializedName("rfcrazonSocial")
    private String rfcRazonSocial;
    @SerializedName("domicilio")
    private String domicilio;
    @SerializedName("clavePeriodo")
    private String clavePeriodo;
    @SerializedName("fechaInicio")
    private String fechaInicio;
    @SerializedName("fechaFin")
    private String fechaFin;
    @SerializedName("fechaInicioIncidencias")
    private String fechaInicioIncidencias;
    @SerializedName("fechaFin_incidencias")
    private String fechaFin_incidencias;
    @SerializedName("centrocClienteId")
    private Integer centrocClienteId;
    @SerializedName("reciboXempleado")
    private List<ReciboXempleado> reciboXempleado;
    @SerializedName("registroPatronal")
    private String registroPatronal;
    @SerializedName("lugar")
    private String lugar;
    @SerializedName("tipoNomina")
    private String tipoNomina;

}
