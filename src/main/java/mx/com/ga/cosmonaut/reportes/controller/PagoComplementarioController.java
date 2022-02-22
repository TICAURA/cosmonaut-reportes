package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.reportes.PagoComplementario;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.services.PagoComplementarioService;

import javax.inject.Inject;

@Controller(value = "/pagoComplementario")
public class PagoComplementarioController {

    @Inject
    private PagoComplementarioService pagoComplementarioService;

    @Operation(summary = "${cosmonaut.controller.listaDinamicaPagoComplementario.reportes.resumen}",
            description = "${cosmonaut.controller.listaDinamicaPagoComplementario.reportes.descripcion}",
            operationId = "listaDinamicaPagoComplementario.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Se obtiene lista dinamica por filtro de pago complementario PPP.")
    @Post("/filtroDinamico")
    public HttpResponse<RespuestaGenerica>
    listaDinamicaPagoComplementario(@Body PagoComplementario pagoComplementario) throws ServiceException {
        return HttpResponse.ok(pagoComplementarioService.listaDinamicaPagoComplementario(pagoComplementario));
    }

    @Operation(summary = "${cosmonaut.controller.empresasClientePrincipal.reportes.resumen}",
            description = "${cosmonaut.controller.empresasClientePrincipal.reportes.descripcion}",
            operationId = "empresasClientePrincipal.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Empresas por cliente principal. PPP.")
    @Get("/empresas/{clientePrincipal}")
    public HttpResponse<RespuestaGenerica>
    empresasClientePrincipal(@PathVariable Integer clientePrincipal) throws ServiceException {
        return HttpResponse.ok(pagoComplementarioService.empresasClientePrincipal(clientePrincipal));
    }

    @Operation(summary = "${cosmonaut.controller.grupoNominaEmpresa.reportes.resumen}",
            description = "${cosmonaut.controller.grupoNominaEmpresa.reportes.descripcion}",
            operationId = "grupoNominaEmpresa.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo nomina por empresa. PPP.")
    @Get("/grupoNomina/{idEmpresa}")
    public HttpResponse<RespuestaGenerica>
    grupoNominaEmpresa(@PathVariable Integer idEmpresa) throws ServiceException {
        return HttpResponse.ok(pagoComplementarioService.grupoNominaEmpresa(idEmpresa));
    }
}
