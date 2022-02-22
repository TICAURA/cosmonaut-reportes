package mx.com.ga.cosmonaut.reportes.model;

import java.util.ArrayList;
import java.util.List;

public class ReporteIncidenciaModel {
    private Integer idEmpresa = 0;
    private List<Integer> listaIdEmpleados = new ArrayList<>();

    public ReporteIncidenciaModel () {}

    public ReporteIncidenciaModel (Integer idEmpresa, List<Integer> listaIdEmpleados) {
        this.idEmpresa = idEmpresa;
        this.listaIdEmpleados = listaIdEmpleados;
    }

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
}
