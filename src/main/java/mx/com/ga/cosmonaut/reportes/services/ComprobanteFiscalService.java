package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;

public interface ComprobanteFiscalService {

    RespuestaGenerica comprobanteFiscal(ComprobanteFiscalModel comprobanteFiscalModel,
                                        Integer cvServicio) throws ServiceException;
}
