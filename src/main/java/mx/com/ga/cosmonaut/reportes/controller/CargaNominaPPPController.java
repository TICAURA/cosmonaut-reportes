package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import mx.com.ga.cosmonaut.reportes.services.CargaNominaPPPService;

import javax.inject.Inject;

@Controller("/cargaMasiva")
public class CargaNominaPPPController {

    @Inject
    private CargaNominaPPPService cargaNominaPPPService;

    @Operation(summary = "${cosmonaut.controller.cargaMasivaNominaPPP.reportes.resumen}",
            description = "${cosmonaut.controller.cargaMasivaNominaPPP.reportes.descripcion}",
            operationId = "cargaMasivaNominaPPP.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Layout - Carga masiva Nomina PPP. (Descarga)")
    @Post("/layoutNominaPPP")
    public HttpResponse<RespuestaGenerica> cargaMasivaNominaPPP(@Body ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException {
        return HttpResponse.ok(cargaNominaPPPService.crearCargaMasivaNominaPPP(reporteIncidenciaModel));
    }
}
