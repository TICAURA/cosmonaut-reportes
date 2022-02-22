package mx.com.ga.cosmonaut.reportes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Nomina {

    @JsonProperty(value = "nominaXperiodoId")
    private Integer nominaXperiodoId;
    private String fechaContrato;
    private Integer personaId;
    private Integer clienteId;
    private Integer usuarioId;

    public Nomina(Integer nominaPeriodoId, String fechaContrato, Integer personaId,
                  Integer clienteId, Integer usuarioId) {
        this.nominaXperiodoId = nominaPeriodoId;
        this.fechaContrato = fechaContrato;
        this.personaId = personaId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;

    }

    public Integer getNominaPeriodoId() {
        return nominaXperiodoId;
    }

    public void setNominaPeriodoId(Integer nominaPeriodoId) {
        this.nominaXperiodoId = nominaPeriodoId;
    }

    public String getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(String fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public Integer getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}