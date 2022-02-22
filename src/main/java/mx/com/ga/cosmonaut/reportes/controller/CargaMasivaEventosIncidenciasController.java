package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;
import mx.com.ga.cosmonaut.reportes.services.CargaMasivaEventosIncidenciasService;

import javax.inject.Inject;

@Controller(value = "/cargaMasiva")
public class CargaMasivaEventosIncidenciasController {

    @Inject
    private CargaMasivaEventosIncidenciasService cargaMasivaEventosIncidenciasService;

    @Operation(summary = "${cosmonaut.controller.cargaMasivaEventosIncidencias.reportes.resumen}",
            description = "${cosmonaut.controller.cargaMasivaEventosIncidencias.reportes.descripcion}",
            operationId = "cargaMasivaEventosIncidencias.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Layout - Carga masiva Eventos-Incidencias. (Descarga)")
    @Post("/layoutEventosIncidencias/")
    public HttpResponse<RespuestaGenerica> cargaMasivaEventosIncidencias(ReporteEventoIncidencia reporteEventoIncidencia)
            throws ServiceException {
        return HttpResponse.ok(cargaMasivaEventosIncidenciasService
                .crearCargaMasivaEventosIncidencias(reporteEventoIncidencia));
    }

}
