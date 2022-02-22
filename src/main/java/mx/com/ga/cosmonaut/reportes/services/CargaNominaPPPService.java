package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;

public interface CargaNominaPPPService {

    RespuestaGenerica crearCargaMasivaNominaPPP(ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException;
}
