package mx.com.ga.cosmonaut.reportes.dto.respuesta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetalleRespuesta {

    @JsonProperty(value = "code")
    private Integer code;
    @JsonProperty(value = "message")
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}