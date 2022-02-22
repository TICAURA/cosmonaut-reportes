package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ImssService {

    RespuestaGenerica variabilidad(Integer variabilidad) throws ServiceException;
}
