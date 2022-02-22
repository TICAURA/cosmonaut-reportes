package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Percepcion {

    @SerializedName("percepciones")
    private String percepciones;
    @SerializedName("importe")
    private String importe;

    public String getPercepciones() {
        return percepciones;
    }

    public void setPercepciones(String percepciones) {
        this.percepciones = percepciones;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }
}
