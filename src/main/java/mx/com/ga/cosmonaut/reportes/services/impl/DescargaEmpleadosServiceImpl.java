package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatMetodoPago;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoCompensacion;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsBanco;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoContrato;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoRegimenContratacion;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatAreaGeografica;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatEstado;
import mx.com.ga.cosmonaut.common.entity.cliente.*;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaEmpleado;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatMetodoPagoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoCompensacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsBancoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoContratoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoRegimenContratacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatAreaGeograficaRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatEstadoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.*;
import mx.com.ga.cosmonaut.common.repository.temporal.CargaMasivaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ConstantesReportes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import mx.com.ga.cosmonaut.reportes.services.CargaEmpleadoService;
import mx.com.ga.cosmonaut.reportes.services.CargaExEmpleadoService;
import mx.com.ga.cosmonaut.reportes.services.CargaPTUService;
import mx.com.ga.cosmonaut.reportes.services.DescargaEmpleadosService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.LISTA_CAMPOS_NUMEROS_EMPLEADO;
import static mx.com.ga.cosmonaut.reportes.util.Constantes.LISTA_CAMPOS_NUMEROS_EMPLEADOPPP;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.formatoCelda;

@Singleton
public class DescargaEmpleadosServiceImpl implements DescargaEmpleadosService {

    @Inject
    private CargaEmpleadoService cargaEmpleadoService;

    @Inject
    private CargaMasivaRepository cargaMasivaRepository;

    @Inject
    private NclAreaRepository nclAreaRepository;

    @Inject
    private NclPuestoRepository nclPuestoRepository;

    @Inject
    private CsTipoContratoRepository csTipoContratoRepository;

    @Inject
    private CatEstadoRepository catEstadoRepository;

    @Inject
    private CsTipoRegimenContratacionRepository csTipoRegimenContratacionRepositor;

    @Inject
    private NclPoliticaRepository nclPoliticaRepository;

    @Inject
    private CatAreaGeograficaRepository catAreaGeograficaRepository;

    @Inject
    private NclJornadaRepository nclJornadaRepository;

    @Inject
    private CatTipoCompensacionRepository catTipoCompensacionRepository;

    @Inject
    private NclGrupoNominaRepository nclGrupoNominaRepository;

    @Inject
    private CatMetodoPagoRepository catMetodoPagoRepository;

    @Inject
    private CsBancoRepository csBancoRepository;

    @Inject
    private CargaPTUService cargaPTUService;

    @Inject
    private CargaExEmpleadoService cargaExEmpleadoService;

    @Override
    public RespuestaGenerica descarga(Long id, Integer tipoCargaId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = obtenReporte(id, tipoCargaId);
            String archivo = (String) respuesta.getDatos();
            InputStream fichero = new ByteArrayInputStream(Utilidades.decodeContent(archivo));
            XSSFWorkbook libro = new XSSFWorkbook(fichero);
            XSSFSheet hoja = libro.getSheetAt(0);
            XSSFCellStyle estilo = creaEstiloError(libro);
            creaCeldaError(tipoCargaId,hoja, estilo);
            List<CargaMasivaEmpleado> lista =
                    cargaMasivaRepository.findByCentrocClienteIdAndEsCorrecto(id.intValue(), Constantes.RESULTADO_ERROR);

            DataFormat formato = libro.createDataFormat();
            CellStyle celdaTexto = libro.createCellStyle();
            celdaTexto.setDataFormat(formato.getFormat("@"));

            CellStyle celdaFecha = libro.createCellStyle();
            CreationHelper createHelper = libro.getCreationHelper();
            celdaFecha.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            for (int i = 0; i < lista.size(); i++) {
                CargaMasivaEmpleado empleado = lista.get(i);
                XSSFRow fila = hoja.createRow(i + 2);
                fila.createCell(ConstantesReportes.CARGA_MASIVA_NUMERO_ID, CellType.STRING).setCellValue(empleado.getNumeroEmpleado());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_NOMBRE_ID, CellType.STRING).setCellValue(empleado.getNombre());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_APPELLIDO_PATERNO_ID, CellType.STRING).setCellValue(empleado.getApellidoPaterno());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_APPELLIDO_MATERNO_ID, CellType.STRING).setCellValue(empleado.getApellidoMaterno());
                //fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO_ID).setCellValue(Utilidades.fechaTexto(empleado.getFechaNacimiento()));
                XSSFCell celdaFechaNacimiento = fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO_ID);
                celdaFechaNacimiento.setCellValue(empleado.getFechaNacimiento());
                celdaFechaNacimiento.setCellStyle(celdaFecha);
                fila.createCell(ConstantesReportes.CARGA_MASIVA_GENERO_ID).setCellValue(obtenGenero(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_RFC_ID).setCellValue(empleado.getRfc());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_CURP_ID).setCellValue(empleado.getCurp());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_NSS_ID, CellType.STRING).setCellValue(empleado.getNss());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_CORREO_PERSONAL_ID).setCellValue(empleado.getContactoInicialEmailPersonal());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL_ID).setCellValue(empleado.getEmailCorporativo());
                fila.createCell(ConstantesReportes.CARGA_MASIVA_CELULAR_ID).setCellValue(Utilidades.bigIntegerLong(empleado.getCelular()));
                //fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_ANTIGUEDAD_ID).setCellValue(Utilidades.fechaTexto(empleado.getFechaAntiguedad()));
                XSSFCell celdaFechaAntiguedad = fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_ANTIGUEDAD_ID);
                celdaFechaAntiguedad.setCellValue(empleado.getFechaAntiguedad());
                celdaFechaAntiguedad.setCellStyle(celdaFecha);
                fila.createCell(ConstantesReportes.CARGA_MASIVA_AREA_ID).setCellValue(obtenArea(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_PUESTO_ID).setCellValue(obtenPuesto(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_TIPO_CONTRATO_ID).setCellValue(obtenTipoContrato(empleado));
                //fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO_ID).setCellValue(Utilidades.fechaTexto(empleado.getFechaInicio()));
                XSSFCell celdaFechaInicio = fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO_ID);
                celdaFechaInicio.setCellValue(empleado.getFechaInicio());
                celdaFechaInicio.setCellStyle(celdaFecha);
                //fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO_ID).setCellValue(Utilidades.fechaTexto(empleado.getFechaFin()));
                XSSFCell celdaFechaFin = fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO_ID);
                celdaFechaFin.setCellValue(empleado.getFechaFin());
                celdaFechaFin.setCellStyle(celdaFecha);
                fila.createCell(ConstantesReportes.CARGA_MASIVA_ENTIDAD_ID).setCellValue(obtenEstado(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_TIPO_EMPLEADO_ID).setCellValue(obtenTipoRegimenContratacion(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_POLITICA_ID).setCellValue(obtenPolitica(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_SINDICALIZADO_ID).setCellValue(empleado.getEsSindicalizado() != null ? Boolean.TRUE.equals(empleado.getEsSindicalizado()) ? "1-Si" : "2-No" : "");
                fila.createCell(ConstantesReportes.CARGA_MASIVA_AREA_GEOGRAFICA_ID).setCellValue(obtenAreaGeografica(empleado));
                fila.createCell(ConstantesReportes.CARGA_MASIVA_JORNADA_ID).setCellValue(obtenJornada(empleado));
                switch (tipoCargaId){
                    case 1:
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_ID -1).setCellValue(empleado.getErrores());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION_ID).setCellValue(obtenTipoCompensacion(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO_ID).setCellValue(validaMonto(empleado.getSueldoBrutoMensual()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION_ID).setCellValue(validaMonto(empleado.getSbc()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA_ID).setCellValue(obtenGrupNomina(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_METODO_PAGO_ID).setCellValue(obtenMetodoPago(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_BANCO_ID).setCellValue(obtenBanco(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA_ID,CellType.STRING).setCellValue(empleado.getNumeroCuenta());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_CLABE_ID,CellType.STRING).setCellValue(empleado.getClabe());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_NUEMRO_CLIENTE_ID).setCellValue(empleado.getNumeroInformacion());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES_ID).setCellValue(obtenDiasVacaciones(empleado));
                        break;
                    case 2:
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION_ID).setCellValue(obtenTipoCompensacion(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO_ID).setCellValue(validaMonto(empleado.getSueldoBrutoMensual()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION_ID).setCellValue(validaMonto(empleado.getSbc()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_GRUPO_NOMINA_ID).setCellValue(obtenGrupNomina(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_METODO_PAGO_ID).setCellValue(obtenMetodoPago(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_BANCO_ID).setCellValue(obtenBanco(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_NUMERO_CUENTA_ID, CellType.STRING).setCellValue(empleado.getNumeroCuenta());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_CLABE_ID, CellType.STRING).setCellValue(empleado.getClabe());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_NUEMRO_CLIENTE_ID).setCellValue(empleado.getNumeroInformacion());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_SALDO_VACACIONES_ID).setCellValue(obtenDiasVacaciones(empleado));
                        XSSFCell celdaFechaBaja = fila.createCell(ConstantesReportes.CARGA_MASIVA_FECHA_BAJA_ID);
                        celdaFechaBaja.setCellValue(empleado.getFechaBaja());
                        celdaFechaBaja.setCellStyle(celdaFecha);
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_EXEMPLEADOS_ID).setCellValue(empleado.getErrores());
                        break;
                    default:
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_TIPO_COMPENSACION_ID).setCellValue(obtenTipoCompensacion(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_SUELDO_NETO_ID).setCellValue(validaMonto(empleado.getSueldoBrutoMensual()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_SBC_ID).setCellValue(validaMonto(empleado.getSbc()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_SALARIO_DIARIO_ID).setCellValue(validaMonto(empleado.getSalarioDiario()));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_GRUPO_NOMINA_ID).setCellValue(obtenGrupNomina(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_METODO_PAGO_ID).setCellValue(obtenMetodoPago(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_BANCO_ID).setCellValue(obtenBanco(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_NUMERO_CUENTA_ID, CellType.STRING).setCellValue(empleado.getNumeroCuenta());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_CLABE_ID, CellType.STRING).setCellValue(empleado.getClabe());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_NUEMRO_CLIENTE_ID).setCellValue(empleado.getNumeroInformacion());
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_PPP_SALDO_VACACIONES_ID).setCellValue(obtenDiasVacaciones(empleado));
                        fila.createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID).setCellValue(empleado.getErrores());
                        break;
                }
                fila.forEach(celda -> {
                    int celdaId = celda.getColumnIndex();
                    switch (celda.getCellType()) {
                        case STRING:
                        case BLANK:
                            switch (tipoCargaId){
                                case 1:
                                    if (mx.com.ga.cosmonaut.reportes.util.Constantes.LISTA_CAMPOS_NUMEROS_EMPLEADO.stream()
                                            .anyMatch(v -> v == celdaId) ){
                                        celda.setCellStyle(celdaTexto);
                                    }
                                    break;
                                case 2:
                                    if (mx.com.ga.cosmonaut.reportes.util.Constantes.LISTA_CAMPOS_NUMEROS_EXEMPLEADO.stream()
                                            .anyMatch(v -> v == celdaId) ){
                                        celda.setCellStyle(celdaTexto);
                                    }
                                    break;
                                default:
                                    if (mx.com.ga.cosmonaut.reportes.util.Constantes.LISTA_CAMPOS_NUMEROS_EMPLEADOPPP.stream()
                                            .anyMatch(v -> v == celdaId) ){
                                        celda.setCellStyle(celdaTexto);
                                    }
                                    break;
                            }
                            break;
                    }
                });
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (bos) {
                libro.write(bos);
            }
            FileOutputStream file = new FileOutputStream("C:\\Users\\Usuario\\Desktop\\ASG\\empleadoErrores.xlsx");
            libro.write(file);
            respuesta.setDatos(bos.toByteArray());

            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" descargaEmleadosErroneos " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private RespuestaGenerica obtenReporte(Long id, Integer tipoCargaId) throws ServiceException {
        switch (tipoCargaId){
            case 1:
                return cargaEmpleadoService.crearReporteEmpleado(id.intValue());
            case 2:
                return cargaExEmpleadoService.crearReporteExEmpleado(id.intValue());
            default:
                return cargaEmpleadoService.crearReporteEmpleadoPPP(id.intValue());
        }
    }

    private XSSFCellStyle creaEstiloError(XSSFWorkbook libro){
        XSSFColor color = new XSSFColor(new java.awt.Color(207,51,48), null);
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setFillForegroundColor(color);
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        return estilo;
    }

    private void creaCeldaError(Integer tipoCargaId, XSSFSheet hoja, XSSFCellStyle estilo) {
        if (tipoCargaId == 1) {
            XSSFCell celdaError = hoja.getRow(0).createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_ID-1);
            celdaError.setCellValue("Errores");
            celdaError.setCellStyle(estilo);
            hoja.addMergedRegion(new CellRangeAddress(0, 1,
                    ConstantesReportes.CARGA_MASIVA_ERRORES_ID-1, ConstantesReportes.CARGA_MASIVA_ERRORES_ID-1));
        } else if (tipoCargaId == 2) {
            XSSFCell celdaErrorPPP = hoja.getRow(0).createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_EXEMPLEADOS_ID);
            celdaErrorPPP.setCellValue("Errores");
            celdaErrorPPP.setCellStyle(estilo);
            hoja.addMergedRegion(new CellRangeAddress(0, 1,
                    ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID, ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID));
        }else  {
            XSSFCell celdaErrorPPP = hoja.getRow(0).createCell(ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID);
            celdaErrorPPP.setCellValue("Errores");
            celdaErrorPPP.setCellStyle(estilo);
            hoja.addMergedRegion(new CellRangeAddress(0, 1,
                    ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID, ConstantesReportes.CARGA_MASIVA_ERRORES_PPP_ID));
        }
    }

    private String obtenGenero(CargaMasivaEmpleado empleado){
        String genero = "";
        if (empleado.getGenero() != null){
            genero = empleado.getGenero() + "-" + Utilidades.genero(empleado.getGenero());
        }
        return genero;
    }

    private String obtenArea(CargaMasivaEmpleado empleado){
        String area = "";
        if (empleado.getAreaId() != null){
            NclArea nclArea = nclAreaRepository.findByAreaId(empleado.getAreaId()).orElse(new NclArea());
            area = nclArea.getAreaId() + "-" +nclArea.getDescripcion();
        }
        return area;
    }

    private String obtenPuesto(CargaMasivaEmpleado empleado){
        String puesto = "";
        if (empleado.getPuestoId() != null){
            NclPuesto nclPuesto = nclPuestoRepository.findById(empleado.getPuestoId()).orElse(new NclPuesto());
            puesto = nclPuesto.getPuestoId() + "-" + nclPuesto.getDescripcion();
        }
        return puesto;
    }

    private String obtenTipoContrato(CargaMasivaEmpleado empleado){
        String tipoContrato = "";
        if (empleado.getTipoContratoId() != null){
            CsTipoContrato contrato = csTipoContratoRepository.findById(empleado.getTipoContratoId().toString()).orElse(new CsTipoContrato());
            tipoContrato = contrato.getTipoContratoId() + "-" + contrato.getDescripcion();
        }
        return tipoContrato;
    }

    private String obtenEstado(CargaMasivaEmpleado empleado){
        String estado = "";
        if (empleado.getEstadoId() != null){
            CatEstado catEstado = catEstadoRepository.findById(empleado.getEstadoId().longValue()).orElse(new CatEstado());
            estado = catEstado.getEstadoId() + "-" + catEstado.getEstado();
        }
        return estado;
    }

    private String obtenTipoRegimenContratacion(CargaMasivaEmpleado empleado){
        String tipoRegimen = "";
        if (empleado.getTipoRegimenContratacionId() != null){
            CsTipoRegimenContratacion regimen = csTipoRegimenContratacionRepositor.findById(empleado.getTipoRegimenContratacionId().toString()).orElse(new
                    CsTipoRegimenContratacion());
            tipoRegimen = regimen.getTipoRegimenContratacionId() + "-" + regimen.getDescripcion();
        }
        return tipoRegimen;
    }

    private String obtenPolitica(CargaMasivaEmpleado empleado){
        String politica = "";
        if (empleado.getPoliticaId() != null){
            NclPolitica nclPolitica = nclPoliticaRepository.findById(empleado.getPoliticaId()).orElse(new NclPolitica());
            politica = nclPolitica.getPoliticaId() + "-" + nclPolitica.getNombre();
        }
        return politica;
    }

    private String obtenAreaGeografica(CargaMasivaEmpleado empleado){
        String area = "";
        if (empleado.getAreaGeograficaId() != null){
            CatAreaGeografica areaGeografica = catAreaGeograficaRepository.findById(empleado.getAreaGeograficaId().longValue()).orElse(new CatAreaGeografica());
            area = areaGeografica.getAreaGeograficaId() + "-" + areaGeografica.getDescripcion();
        }
        return area;
    }

    private String obtenJornada(CargaMasivaEmpleado empleado){
        String jornada = "";
        if (empleado.getJornadaId() != null){
            NclJornada nclJornada = nclJornadaRepository.findById(empleado.getJornadaId()).orElse(new NclJornada());
            jornada = nclJornada.getJornadaId() + "-" + nclJornada.getTipoJornadaId().getTipoJornadaId() + "-" + nclJornada.getNombre();
        }
        return jornada;
    }

    private String obtenTipoCompensacion(CargaMasivaEmpleado empleado){
        String tipoCompensacion = "";
        if (empleado.getTipoCompensacionId() != null){
            CatTipoCompensacion compensacion = catTipoCompensacionRepository.findById(empleado.getTipoCompensacionId().longValue()).orElse(new CatTipoCompensacion());
            tipoCompensacion = compensacion.getTipoCompensacionId() + "-" + compensacion.getDescripcion();
        }
        return tipoCompensacion;
    }

    private String obtenGrupNomina(CargaMasivaEmpleado empleado){
        String grupoNomina = "";
        if (empleado.getGrupoNominaId() != null){
            NclGrupoNomina nclGrupoNomina = nclGrupoNominaRepository.findByGrupoNominaId(empleado.getGrupoNominaId()).orElse(new NclGrupoNomina());
            grupoNomina = nclGrupoNomina.getGrupoNominaId() + "-" + nclGrupoNomina.getNombre();
        }
        return grupoNomina;
    }

    private String obtenMetodoPago(CargaMasivaEmpleado empleado){
        String metodoPago = "";
        if (empleado.getMetodoPagoId() != null){
            CatMetodoPago catMetodoPago = catMetodoPagoRepository.findById(empleado.getMetodoPagoId()).orElse(new CatMetodoPago());
            metodoPago = catMetodoPago.getMetodoPagoId() + "-" + catMetodoPago.getDescripcion();
        }
        return metodoPago;
    }

    private Integer obtenDiasVacaciones(CargaMasivaEmpleado empleado){
        Integer diasVacaciones = 0;
        if (empleado.getDiasVacaciones() != null){
            diasVacaciones = empleado.getDiasVacaciones();
        }
        return diasVacaciones;
    }

    private String obtenBanco(CargaMasivaEmpleado empleado){
        String banco = "";
        if (empleado.getBancoId() != null){
            CsBanco csBanco = csBancoRepository.findById(empleado.getBancoId().longValue()).orElse(new CsBanco());
            banco = csBanco.getBancoId() + "-" + csBanco.getNombreCorto();
        }
        return banco;
    }

    private Double validaMonto(Double monto){
        if (monto != null){
            return monto;
        }else {
            return 0.0;
        }
    }

}
