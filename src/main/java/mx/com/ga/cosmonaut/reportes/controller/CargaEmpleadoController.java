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
import mx.com.ga.cosmonaut.reportes.model.TipoCarga;
import mx.com.ga.cosmonaut.reportes.services.CargaEmpleadoService;
import mx.com.ga.cosmonaut.reportes.services.CargaExEmpleadoService;

import javax.inject.Inject;

@Controller("/cargaMasiva")
public class CargaEmpleadoController {


    @Inject
    private CargaEmpleadoService cargaEmpleadoService;

    @Inject
    private CargaExEmpleadoService cargaExEmpleadoService;

    @Operation(summary = "${cosmonaut.controller.cargaEmpleado.reportes.resumen}",
            description = "${cosmonaut.controller.cargaEmpleado.reportes.descripcion}",
            operationId = "cargaEmpleado.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reporte - Carga masiva empleado por empresa / Ex empleados / Empleados PPP . (Descarga)")
    @Post("/layoutEmpleado/")
    public HttpResponse<RespuestaGenerica> crearReporteEmpleado(@Body TipoCarga tipoCarga) throws ServiceException {
        switch (tipoCarga.getTipoCargaId()) {
            /** Empleados. */
            case 1: return HttpResponse
                    .ok(cargaEmpleadoService.crearReporteEmpleado(tipoCarga.getIdEmpresa()));

            /** Ex empleados. */
            case 2: return HttpResponse
                    .ok(cargaExEmpleadoService.crearReporteExEmpleado(tipoCarga.getIdEmpresa()));

            /** Empleados ppp. */
            case 3: return HttpResponse
                    .ok(cargaEmpleadoService.crearReporteEmpleadoPPP(tipoCarga.getIdEmpresa()));
            default: return null;
        }
    }

    @Operation(summary = "${cosmonaut.controller.crearReporteListaEmpleado.reportes.resumen}",
            description = "${cosmonaut.controller.crearReporteListaEmpleado.reportes.descripcion}",
            operationId = "crearReporteListaEmpleado.reportes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Layout - Carga masiva Lista empleados por empresa. (Descarga)")
    @Get("/layoutListaEmpleado/{idEmpresa}")
    public HttpResponse<RespuestaGenerica> crearReporteListaEmpleado(@PathVariable Integer idEmpresa) throws ServiceException {
        return HttpResponse.ok(cargaEmpleadoService.crearReporteListaEmpleado(idEmpresa));
    }
}
