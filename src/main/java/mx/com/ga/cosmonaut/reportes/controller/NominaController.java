package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;
import mx.com.ga.cosmonaut.reportes.services.NominaService;

import javax.inject.Inject;

@Controller("/nominaDispersion")
public class NominaController {

    @Inject
    private NominaService nominaService;

    @Operation(summary = "${cosmonaut.controller.nominaDispersion.reportes.resumen}",
            description = "${cosmonaut.controller.nominaDispersion.reportes.descripcion}",
            operationId = "nominaDispersion.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersión - Archivo RFC. (Descarga)")
    @Get("/txtIdRfc/{nominaXperiodoId}")
    public HttpResponse<RespuestaGenerica> crearArchivoNomina(@PathVariable Integer nominaXperiodoId) throws ServiceException {
        return HttpResponse.ok(nominaService.crearArchivoRFC(nominaXperiodoId));
    }

    @Operation(summary = "${cosmonaut.controller.crearLayoutDispersionNomina.reportes.resumen}",
            description = "${cosmonaut.controller.crearLayoutDispersionNomina.reportes.descripcion}",
            operationId = "crearLayoutDispersionNomina.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Dispersión Nomina. (Descarga)")
    @Post("/layoutDispersionNomina/")
    public HttpResponse<RespuestaGenerica> crearLayoutDispersionNomina
            (@Body ComprobanteFiscalModel comprobanteFiscalModel) throws ServiceException {
        return HttpResponse.ok(nominaService.crearLayoutDispersionNomina(comprobanteFiscalModel));
    }

    @Operation(summary = "${cosmonaut.controller.crearLayoutDinamicoNominaExtraordinaria.reportes.resumen}",
            description = "${cosmonaut.controller.crearLayoutDinamicoNominaExtraordinaria.reportes.descripcion}",
            operationId = "crearLayoutDinamicoNominaExtraordinaria.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Nómina Extraordinaria. (Descarga)")
    @Get("/layoutNominaExtraordinaria/{idEmpresa}")
    public HttpResponse<RespuestaGenerica> crearLayoutDinamicoNominaExtraordinaria(@PathVariable Integer idEmpresa) throws ServiceException {
        return HttpResponse.ok(nominaService.crearLayoutDinamicoNominaExtraordinaria(idEmpresa));
    }
    
    @Operation(summary = "${cosmonaut.controller.reporteNomina.reportes.resumen}",
            description = "${cosmonaut.controller.reporteNomina.reportes.descripcion}",
            operationId = "reporteNomina.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Reporte Nómina. (Descarga)")
    @Post("/layoutReporteNomina/")
    public HttpResponse<RespuestaGenerica> reporteNomina(
            @Body ComprobanteFiscalModel comprobanteFiscalModel) throws ServiceException {
        return HttpResponse.ok(nominaService.reporteNomina(comprobanteFiscalModel,Boolean.FALSE));
    }

    @Operation(summary = "${cosmonaut.controller.reporteNominaExtraordinariaAguinaldo.reportes.resumen}",
            description = "${cosmonaut.controller.reporteNominaExtraordinariaAguinaldo.reportes.descripcion}",
            operationId = "reporteNominaExtraordinariaAguinaldo.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Reporte Nómina Extraordinaria Aguinaldo. (Descarga)")
    @Post("/layoutReporteNominaExtraordinariaAguinaldo/")
    public HttpResponse<RespuestaGenerica> reporteNominaExtraordinariaAguinaldo(
            @Body ComprobanteFiscalModel comprobanteFiscalModel) throws ServiceException {
        return HttpResponse.ok(nominaService.reporteNomina(comprobanteFiscalModel,Boolean.TRUE));
    }
}
