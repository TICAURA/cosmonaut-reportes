package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.dto.SuaModel;

public interface SuaService {

    RespuestaGenerica suaAltas(SuaModel suaModel) throws ServiceException;

    RespuestaGenerica suaModificacion(SuaModel suaModel) throws ServiceException;
}
