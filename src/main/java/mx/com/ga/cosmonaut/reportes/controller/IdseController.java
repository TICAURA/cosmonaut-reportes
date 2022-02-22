package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.IdseModel;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.service.IdseService;
import mx.com.ga.cosmonaut.common.util.Utilidades;

import javax.inject.Inject;

@Controller(value = "/idse")
public class IdseController {

    @Inject
    private IdseService idseService;

    @Operation(summary = "${cosmonaut.controller.empleado.idseConsulta.resumen}",
            description = "${cosmonaut.controller.empleado.idseConsulta.descripcion}",
            operationId = "empleado.idseConsulta")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes txt - IDSE.")
    @Post(value = "/consulta",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> idseConsulta(@Body IdseModel idseModel){

        try {
            return HttpResponse.ok(idseService.idseConsulta(idseModel, Boolean.FALSE));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.empleado.consultaCentroCostoPadre.resumen}",
            description = "${cosmonaut.controller.empleado.consultaCentroCostoPadre.descripcion}",
            operationId = "empleado.consultaCentroCostoPadre")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes txt - IDSE Centro cliente Padre.")
    @Post(value = "/consultaCentroCostoPadre",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> idseConsultaCentroClientePadre(@Body IdseModel idseModel){
        try {
            return HttpResponse.ok(idseService.idseConsulta(idseModel, Boolean.TRUE));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
