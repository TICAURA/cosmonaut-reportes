package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.services.CargaEventoService;

import javax.inject.Inject;

@Controller("/cargaMasiva")
public class CargaEventoController {

    @Inject
    private CargaEventoService cargaEventoService;

    @Operation(summary = "${cosmonaut.controller.cargaEvento.reportes.resumen}",
            description = "${cosmonaut.controller.cargaEvento.reportes.descripcion}",
            operationId = "cargaEvento.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Layout - Carga masiva evento. (Descarga)")
    @Get("/layoutEvento")
    public HttpResponse<RespuestaGenerica> cargaMasivaEvento() throws ServiceException {
        return HttpResponse.ok(cargaEventoService.creaReporteEvento());
    }

}
