package mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladoconcepto;

import lombok.Data;

import java.util.List;

@Data
public class ReporteNominaHistorica {

    private String registroPatronalImss;
    private String rfc;
    private String razonSocial;
    private String periodo;
    private List<Provisiones> provisiones;
    private List<Percepciones> percepciones;
    private List<Deducciones> deducciones;
    private String netoApagarEnefectivo;
    private String netoApagar_enespecie;
    private String netoApagar;
}
