package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;
import mx.com.ga.cosmonaut.reportes.services.ComprobanteFiscalService;

import javax.inject.Inject;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;

@Controller("/comprobanteFiscal")
public class ComprobanteFiscalController {

    @Inject
    private ComprobanteFiscalService comprobanteFiscalService;

    @Operation(summary = "${cosmonaut.controller.comprobanteFiscalOrdinaria.reportes.resumen}",
            description = "${cosmonaut.controller.comprobanteFiscalOrdinaria.reportes.descripcion}",
            operationId = "comprobanteFiscalOrdinaria.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - ComprobanteFiscalOrdinaria - vista previa / paquete. ")
    @Post(value = "/layoutComprobanteFiscalOrdinaria/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> comprobanteFiscalOrdinaria(@Body ComprobanteFiscalModel comprobanteFiscalModel)
            throws ServiceException {
        return HttpResponse.ok(comprobanteFiscalService
                .comprobanteFiscal(comprobanteFiscalModel, CVE_SERVICIO_28));
    }

    @Operation(summary = "${cosmonaut.controller.comprobanteFiscalExtraordinaria.reportes.resumen}",
            description = "${cosmonaut.controller.comprobanteFiscalExtraordinaria.reportes.descripcion}",
            operationId = "comprobanteFiscalExtraordinaria.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - ComprobanteFiscalExtraordinaria - vista previa / paquete. ")
    @Post(value = "/layoutComprobanteFiscalExtraordinaria/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> comprobanteFiscalExtraordinaria(@Body ComprobanteFiscalModel comprobanteFiscalModel)
            throws ServiceException {
        return HttpResponse.ok(comprobanteFiscalService
                .comprobanteFiscal(comprobanteFiscalModel, CVE_SERVICIO_39));

    }

}
