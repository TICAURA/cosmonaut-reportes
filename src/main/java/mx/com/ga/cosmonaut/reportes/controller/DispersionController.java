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
import mx.com.ga.cosmonaut.reportes.services.DispersionServices;

import javax.inject.Inject;
import java.util.List;

@Controller("/dispersion")
public class DispersionController {

    @Inject
    private DispersionServices dispersionServices;

    @Operation(summary = "${cosmonaut.controller.dispersion.lista.errores.resumen}",
            description = "${cosmonaut.controller.dispersion.lista.errores.descripcion}",
            operationId = "dispersion.listaerrores")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Lista Errores")
    @Post(value = "/lista/errores/{nominaPeriodoId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaEmleadosErroneos(@PathVariable Integer nominaPeriodoId, @Body List<Integer> listaPersonas){
        try {
            return HttpResponse.ok(dispersionServices.generaListaEmpleadosDispersion(nominaPeriodoId,listaPersonas));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
