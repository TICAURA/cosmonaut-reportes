package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface CargaViaticoService {

    RespuestaGenerica crearCargaMasivaViatico() throws ServiceException;
}
