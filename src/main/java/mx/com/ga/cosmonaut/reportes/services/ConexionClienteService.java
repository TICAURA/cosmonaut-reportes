package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ConexionClienteService {

    RespuestaGenerica conectarClienteServicio(Integer nominaXperiodoId, Integer cveServicio) throws ServiceException;

    RespuestaGenerica conectarClienteServicio(Object object, Integer cveServicio) throws ServiceException;

    RespuestaGenerica conectarClienteServicioImss(Integer variabilidad, Integer cveServicio) throws ServiceException;

}
