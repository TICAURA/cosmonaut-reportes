package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface CargaEmpleadoService {

    RespuestaGenerica crearReporteEmpleado(Integer idEmpresa) throws ServiceException;

    RespuestaGenerica crearReporteEmpleadoPPP(Integer idEmpresa) throws ServiceException;

    RespuestaGenerica
    crearReporteListaEmpleado(Integer idEmpresa) throws ServiceException;
}
