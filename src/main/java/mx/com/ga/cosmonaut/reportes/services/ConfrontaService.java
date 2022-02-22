package mx.com.ga.cosmonaut.reportes.services;

import io.micronaut.http.multipart.CompletedFileUpload;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.confronta.FiltradoRequest;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ConfrontaService {

    RespuestaGenerica guardar(/*GuardarRequest request, */CompletedFileUpload file) throws ServiceException;

    RespuestaGenerica filtrar(FiltradoRequest request) throws ServiceException;

    RespuestaGenerica descargar(Long id) throws ServiceException;

}
