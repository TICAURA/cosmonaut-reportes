package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.reportes.PagoComplementario;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PagoComplementarioService {

    RespuestaGenerica listaDinamicaPagoComplementario(PagoComplementario pagoComplementario) throws ServiceException;

    RespuestaGenerica empresasClientePrincipal(Integer clientePrincipal) throws ServiceException;

    RespuestaGenerica grupoNominaEmpresa(Integer idEmpresa) throws ServiceException;

}
