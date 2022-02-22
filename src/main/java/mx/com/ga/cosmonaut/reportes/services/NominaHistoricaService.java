package mx.com.ga.cosmonaut.reportes.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoConcepto;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoHistorica;
import mx.com.ga.cosmonaut.reportes.model.FolioFiscal;

public interface NominaHistoricaService {

    RespuestaGenerica polizaContable(Integer nominaXperiodoId) throws ServiceException;

    RespuestaGenerica reporteRaya(Integer nominaXperiodoId) throws ServiceException;

    RespuestaGenerica acumuladoConcepto(AcumuladoConcepto acumuladoConcepto) throws ServiceException;

    RespuestaGenerica folioFiscal(FolioFiscal folioFiscal) throws ServiceException;

    RespuestaGenerica detalleNominaHistorica(Integer nominaXperiodoId) throws ServiceException;

    RespuestaGenerica acumuladoNominaHistorica(AcumuladoHistorica acumuladoHistorica) throws ServiceException;
}
