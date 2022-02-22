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
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.dto.SuaModel;
import mx.com.ga.cosmonaut.reportes.services.SuaService;

import javax.inject.Inject;

@Controller(value = "/sua")
public class SuaController {

    @Inject
    private SuaService  suaService;

    @Operation(summary = "${cosmonaut.controller.empleado.suaAltas.resumen}",
            description = "${cosmonaut.controller.empleado.suaAltas.descripcion}",
            operationId = "empleado.suaAltas")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes txt - SUA Altas.")
    @Post(value = "/altas",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> suaAltas(@Body SuaModel suaModel){
        try {
            return HttpResponse.ok(suaService.suaAltas(suaModel));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.empleado.suaModificacion.resumen}",
            description = "${cosmonaut.controller.empleado.suaModificacion.descripcion}",
            operationId = "empleado.suaModificacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes txt - SUA Modificación.")
    @Post(value = "/modificacion",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> suaModificacion(@Body SuaModel suaModel){
        try {
            return HttpResponse.ok(suaService.suaModificacion(suaModel));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
