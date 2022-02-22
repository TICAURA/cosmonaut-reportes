package mx.com.ga.cosmonaut.reportes.model;

import java.util.ArrayList;
import java.util.List;

public class ReporteEventoIncidencia {

    private Integer idEmpresa;
    private List<Integer> listaIdEmpleados = new ArrayList<>();
    private Boolean esActivo;

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public List<Integer> getListaIdEmpleados() {
        return listaIdEmpleados;
    }

    public void setListaIdEmpleados(List<Integer> listaIdEmpleados) {
        this.listaIdEmpleados = listaIdEmpleados;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }
}
