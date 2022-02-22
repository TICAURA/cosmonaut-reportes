package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ReciboNominaImpl {

    @SerializedName("reciboNomina")
    private ReciboNomina reciboNomina;

    public ReciboNomina getReciboNomina() {
        return reciboNomina;
    }

    public void setReciboNomina(ReciboNomina reciboNomina) {
        this.reciboNomina = reciboNomina;
    }
}
