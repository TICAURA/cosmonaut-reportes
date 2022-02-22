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
import mx.com.ga.cosmonaut.reportes.services.CargaPTUService;

import javax.inject.Inject;

@Controller("/cargaMasiva")
public class CargaPTUController {

    @Inject
    private CargaPTUService cargaPTUService;

    @Operation(summary = "${cosmonaut.controller.cargaMasivaPTU.reportes.resumen}",
            description = "${cosmonaut.controller.cargaMasivaPTU.reportes.descripcion}",
            operationId = "cargaMasivaPTU.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Carga masiva PTU. (Descarga Layout)")
    @Post("/layoutPTU/")
    public HttpResponse<RespuestaGenerica> cargaMasivaPTU(@Body ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException {
        return HttpResponse.ok(cargaPTUService.crearCargaMasivaPTU(reporteIncidenciaModel));
    }
}
