package mx.com.ga.cosmonaut.reportes.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.services.DescargaEmpleadosService;
import mx.com.ga.cosmonaut.reportes.services.EmpleadoService;

import javax.inject.Inject;

@Controller("/empleado")
public class EmpleadoController {

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private DescargaEmpleadosService descargaEmleadosErroneos;

    @Operation(summary = "${cosmonaut.controller.empleado.perfilpersonal.resumen}",
            description = "${cosmonaut.controller.empleado.perfilpersonal.descripcion}",
            operationId = "empleado.perfilpersonal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes - Perfil Personal")
    @Post(value = "/perfil/personal/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> generaPerfilPersonal(@Body NcoContratoColaborador colaborador){
        try {
            return HttpResponse.ok(empleadoService.generaPerfilPersonal(colaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.empleado.listaRecuento.resumen}",
            description = "${cosmonaut.controller.empleado.listaRecuento.descripcion}",
            operationId = "empleado.listaRecuento")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes - Lista Recuento")
    @Get(value = "/recuento/empleados/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> generaRecuentoEmpleados(@PathVariable Long id){
        try {
            return HttpResponse.ok(empleadoService.generaRecuentoEmpleados(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.empleado.listaempleados.resumen}",
            description = "${cosmonaut.controller.empleado.listaempleados.descripcion}",
            operationId = "empleado.listaempleados")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes - Lista Empleados")
    @Get(value = "/lista/empleados/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> generaListaEmpleados(@PathVariable Long id){
        try {
            return HttpResponse.ok(empleadoService.generaListaEmpleados(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.empleado.descargaerroneos.resumen}",
            description = "${cosmonaut.controller.empleado.descargaerroneos.descripcion}",
            operationId = "empleado.descargaerroneos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Reportes - Lista Empleados Erroneos")
    @Get(value = "/lista/empleados/erroneos/{id}/{tipoCargaId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaEmleadosErroneos(@PathVariable Long id,
                                                                    @PathVariable Integer tipoCargaId){
        try {
            return HttpResponse.ok(descargaEmleadosErroneos.descarga(id,tipoCargaId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
