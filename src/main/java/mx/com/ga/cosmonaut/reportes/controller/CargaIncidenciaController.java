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
import mx.com.ga.cosmonaut.reportes.services.CargaIncidenciaService;

import javax.inject.Inject;

@Controller("/cargaMasiva")
public class CargaIncidenciaController {

    @Inject
    private CargaIncidenciaService cargaIncidenciaService;

    @Operation(summary = "${cosmonaut.controller.cargaIncidencia.reportes.resumen}",
            description = "${cosmonaut.controller.cargaIncidencia.reportes.descripcion}",
            operationId = "cargaIncidencia.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Carga masiva incidencia. (Descarga Layout)")
    @Post(value = "/layoutIncidencia/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> crearReporteIncidencia(@Body ReporteIncidenciaModel incidenciaModel)
            throws ServiceException {
        return HttpResponse.ok(cargaIncidenciaService.crearReporteIncidencia(incidenciaModel));
    }
}
