package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface EmpleadoService {

    RespuestaGenerica generaPerfilPersonal(NcoContratoColaborador colaborador) throws ServiceException;

    RespuestaGenerica generaListaEmpleados(Long id) throws ServiceException;

    RespuestaGenerica generaRecuentoEmpleados(Long id) throws ServiceException;

}
