package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Deduccion {
    @SerializedName("deducciones")
    private String deducciones;
    @SerializedName("importe")
    private String importe;

    public String getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(String deduccion) {
        this.deducciones = deduccion;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }
}
