package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface TimbradoService {

    RespuestaGenerica listaErrores(Integer nominaPeriodoId, List<Integer> listaPersonas) throws ServiceException;

}
