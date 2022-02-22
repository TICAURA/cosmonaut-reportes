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
import mx.com.ga.cosmonaut.reportes.services.ImssService;

import javax.inject.Inject;

@Controller(value = "/imss")
public class ImssController {

    @Inject
    private ImssService imssService;

    @Operation(summary = "${cosmonaut.controller.variabilidad.reportes.resumen}",
            description = "${cosmonaut.controller.variabilidad.reportes.descripcion}",
            operationId = "variabilidad.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes - Promedio de variabilidad (Descarga).")
    @Get(value = "/variabilidad/{variabilidad}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> variabilidad(@PathVariable Integer variabilidad){
        try {
            return HttpResponse.ok(imssService.variabilidad(variabilidad));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
