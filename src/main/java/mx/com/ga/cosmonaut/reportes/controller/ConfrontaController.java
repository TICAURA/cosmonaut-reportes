package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.confronta.FiltradoRequest;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.services.ConfrontaService;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller("/confronta")
public class ConfrontaController {

    @Inject
    private ConfrontaService confrontaService;

    @Operation(summary = "${cosmonaut.controller.confronta.guardar.resumen}",
            description = "${cosmonaut.controller.confronta.guardar.descripcion}",
            operationId = "confronta.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Confronta - Guardado y generacion de confronta.")
    @Put(value = "/guardar", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(/*@Body @Valid GuardarRequest request, */@Parameter(value = "file") CompletedFileUpload file) {
        try {
            return HttpResponse.ok(confrontaService.guardar(/*request, */file));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.confronta.filtrar.resumen}",
            description = "${cosmonaut.controller.confronta.filtrar.descripcion}",
            operationId = "confronta.filtrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Confronta - Filtrado.")
    @Post(value = "/filtrar", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtrar(@Body @Valid FiltradoRequest request) {
        try {
            return HttpResponse.ok(confrontaService.filtrar(request));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.confronta.descargar.resumen}",
            description = "${cosmonaut.controller.confronta.descargar.descripcion}",
            operationId = "confronta.descargar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Confronta - Descargar.")
    @Get(value = "/descargar/{id}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargar(@PathVariable @Valid Long id) {
        try {
            return HttpResponse.ok(confrontaService.descargar(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
