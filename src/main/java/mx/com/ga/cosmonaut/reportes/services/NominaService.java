package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;

public interface NominaService {

    RespuestaGenerica crearArchivoRFC(Integer nominaXperiodoId) throws ServiceException;

    RespuestaGenerica crearLayoutDispersionNomina(ComprobanteFiscalModel comprobanteFiscalModel) throws ServiceException;

    RespuestaGenerica crearLayoutDinamicoNominaExtraordinaria(Integer idEmpresa) throws ServiceException;

    RespuestaGenerica reporteNomina(ComprobanteFiscalModel comprobanteFiscalModel, Boolean esExtraordinaria) throws ServiceException;

}
