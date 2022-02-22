package mx.com.ga.cosmonaut.reportes.model;

import lombok.Data;

import java.util.List;

@Data
public class FolioFiscal {

    private Integer nominaXperiodoId;
    private List<Integer> listaIdPersona;

}
