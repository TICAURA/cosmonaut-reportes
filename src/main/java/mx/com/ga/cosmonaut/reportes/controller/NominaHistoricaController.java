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
import mx.com.ga.cosmonaut.reportes.model.AcumuladoConcepto;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoHistorica;
import mx.com.ga.cosmonaut.reportes.model.FolioFiscal;
import mx.com.ga.cosmonaut.reportes.services.NominaHistoricaService;

import javax.inject.Inject;

@Controller(value = "/nominaHistorica")
public class NominaHistoricaController {

    @Inject
    private NominaHistoricaService nominaHistoricaService;


    @Operation(summary = "${cosmonaut.controller.polizaContable.reportes.resumen}",
            description = "${cosmonaut.controller.polizaContable.reportes.descripcion}",
            operationId = "polizaContable.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Poliza contable. (Descarga Layout)")
    @Get("/polizaContable/{nominaXperiodoId}")
    public HttpResponse<RespuestaGenerica> polizaContable(@PathVariable Integer nominaXperiodoId) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.polizaContable(nominaXperiodoId));
    }

    @Operation(summary = "${cosmonaut.controller.reporteRaya.reportes.resumen}",
            description = "${cosmonaut.controller.reporteRaya.reportes.descripcion}",
            operationId = "reporteRaya.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Reporte Raya. (Descarga Layout)")
    @Get("/reporteRaya/{nominaXperiodoId}")
    public HttpResponse<RespuestaGenerica> reporteRaya(@PathVariable Integer nominaXperiodoId) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.reporteRaya(nominaXperiodoId));
    }

    @Operation(summary = "${cosmonaut.controller.acumuladoConcepto.reportes.resumen}",
            description = "${cosmonaut.controller.acumuladoConcepto.reportes.descripcion}",
            operationId = "acumuladoConcepto.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Acumulado por Concepto. (Descarga pdf)")
    @Post("/acumuladoConcepto")
    public HttpResponse<RespuestaGenerica> acumuladoConcepto(@Body AcumuladoConcepto acumuladoConcepto) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.acumuladoConcepto(acumuladoConcepto));
    }

    @Operation(summary = "${cosmonaut.controller.folioFiscal.reportes.resumen}",
            description = "${cosmonaut.controller.folioFiscal.reportes.descripcion}",
            operationId = "folioFiscal.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Folio Fiscal. (Descarga pdf)")
    @Post("/folioFiscal")
    public HttpResponse<RespuestaGenerica> folioFiscal(@Body FolioFiscal folioFiscal) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.folioFiscal(folioFiscal));
    }

    @Operation(summary = "${cosmonaut.controller.detalleNominaHistorica.reportes.resumen}",
            description = "${cosmonaut.controller.detalleNominaHistorica.reportes.descripcion}",
            operationId = "detalleNominaHistorica.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
   
    @Tag(name = "Reporte - Detalle nomina historica. (Descarga pdf)")
    @Get("/detalleNominaHistorica/{nominaXperiodoId}")
    public HttpResponse<RespuestaGenerica> detalleNominaHistorica(@PathVariable Integer nominaXperiodoId) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.detalleNominaHistorica(nominaXperiodoId));
    }

    @Operation(summary = "${cosmonaut.controller.acumuladoNominaHistorica.reportes.resumen}",
            description = "${cosmonaut.controller.acumuladoNominaHistorica.reportes.descripcion}",
            operationId = "acumuladoNominaHistorica.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Acumulado nómina historica. (Descarga Layout)")
    @Post("/acumuladoNominaHistorica/")
    public HttpResponse<RespuestaGenerica> acumuladoNominaHistorica(@Body AcumuladoHistorica acumuladoHistorica) throws ServiceException {
        return HttpResponse.ok(nominaHistoricaService.acumuladoNominaHistorica(acumuladoHistorica));
    }
}
