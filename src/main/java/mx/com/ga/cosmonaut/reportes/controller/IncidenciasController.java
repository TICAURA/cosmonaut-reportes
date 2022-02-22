package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;
import mx.com.ga.cosmonaut.reportes.services.IncidenciasService;

import javax.inject.Inject;

@Controller("/incidencias")
public class IncidenciasController {

    @Inject
    private IncidenciasService incidenciasServices;

    @Operation(summary = "${cosmonaut.controller.incidencias.descargaincidenciaserroneas.resumen}",
            description = "${cosmonaut.controller.incidencias.descargaincidenciaserroneas.descripcion}",
            operationId = "incidencias.descargaincidenciaserroneas")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Descarga incidencias erroneas")
    @Post(value = "/lista/erroneas/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaIncidenciasErroneas(@Body ReporteEventoIncidencia reporteEventoIncidencia){
        try {
            return HttpResponse.ok(incidenciasServices.descargaEmleadosErroneos(reporteEventoIncidencia));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
