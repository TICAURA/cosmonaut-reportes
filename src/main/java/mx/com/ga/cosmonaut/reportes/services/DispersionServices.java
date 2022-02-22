package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface DispersionServices {

    RespuestaGenerica generaListaEmpleadosDispersion(Integer nominaPeriodoId, List<Integer> listaPersonas) throws ServiceException;

}
