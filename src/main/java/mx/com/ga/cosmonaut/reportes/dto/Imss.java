package mx.com.ga.cosmonaut.reportes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Imss {
    @JsonProperty(value = "variabilidad")
    private Integer variabilidad;

    public Imss(Integer variabilidad) {
        this.variabilidad = variabilidad;
    }
}
