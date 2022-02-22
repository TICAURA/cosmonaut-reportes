package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;

public interface IncidenciasService {

    RespuestaGenerica descargaEmleadosErroneos(ReporteEventoIncidencia reporteEventoIncidencia) throws ServiceException;

}
