package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoIncapacidad;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatUnidad;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaIncidencias;
import mx.com.ga.cosmonaut.common.enums.CatTipoIncidenciaEnum;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoIncapacidadRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatUnidadRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.temporal.CargaMasivaIncidenciasRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ConstantesReportes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;
import mx.com.ga.cosmonaut.reportes.services.CargaMasivaEventosIncidenciasService;
import mx.com.ga.cosmonaut.reportes.services.IncidenciasService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.data.general.SeriesException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Singleton
public class IncidenciasServiceImpl implements IncidenciasService {

    @Inject
    private CargaMasivaEventosIncidenciasService cargaMasivaEventosIncidenciasService;

    @Inject
    private CargaMasivaIncidenciasRepository cargaMasivaIncidenciasRepository;

    @Inject
    private CatTipoIncapacidadRepository catTipoIncapacidadRepository;

    @Inject
    private CatUnidadRepository catUnidadRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica descargaEmleadosErroneos(ReporteEventoIncidencia reporteEventoIncidencia)
            throws ServiceException {
        try {

            RespuestaGenerica respuesta = cargaMasivaEventosIncidenciasService
                    .crearCargaMasivaEventosIncidencias(reporteEventoIncidencia);
            InputStream fichero = new ByteArrayInputStream(Utilidades.decodeContent((String) respuesta.getDatos()));
            XSSFWorkbook libro = new XSSFWorkbook(fichero);
            XSSFSheet hoja = libro.getSheetAt(0);
            hoja.autoSizeColumn(ConstantesReportes.INCIDENCIAS_ERRORES_ID);
            hoja.getColumnWidth(20);
            XSSFCellStyle estilo = creaEstiloError(libro);
            creaCeldaError(hoja, estilo);

            XSSFCellStyle estiloCeldaFecha = libro.createCellStyle();
            CreationHelper createHelper = libro.getCreationHelper();
            estiloCeldaFecha.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            for (int i = 0; i <= hoja.getLastRowNum(); i++) {
                XSSFRow renglon = hoja.getRow(i);
                if (renglon.getRowNum() != 0){
                    hoja.removeRow(renglon);
                }
            }

            List<CargaMasivaIncidencias> lista = cargaMasivaIncidenciasRepository.
                    findByCentrocClienteIdAndEsCorrecto(
                            reporteEventoIncidencia.getIdEmpresa(),Constantes.RESULTADO_ERROR);
            for (int i = 0; i < lista.size(); i++) {
                CargaMasivaIncidencias incidencia = lista.get(i);
                XSSFRow fila = hoja.createRow(i + 1);
                fila.createCell(ConstantesReportes.INCIDENCIAS_NUMERO_EMPLEADO_ID, CellType.STRING)
                        .setCellValue(Utilidades.validaString(incidencia.getNumeroEmpleado()));

                NcoContratoColaborador colaborador = ncoContratoColaboradorRepository
                        .findByCentrocClienteIdCentrocClienteIdAndNumEmpleado(
                                incidencia.getCentrocClienteId(),incidencia.getNumeroEmpleado())
                        .orElseThrow(() -> new SeriesException(Constantes.ERROR_OBTENER_EMPLEADO));

                fila.createCell(ConstantesReportes.INCIDENCIAS_NOMBRE_EMPLEADO_ID, CellType.STRING)
                        .setCellValue(colaborador.getPersonaId().getNombre() + " " +
                                colaborador.getPersonaId().getApellidoPaterno());

                fila.createCell(ConstantesReportes.INCIDENCIAS_TIPO_EVENTO_ID)
                        .setCellValue(obtenTipoIncidencia(incidencia.getTipoEventoId()));

                fila.createCell(ConstantesReportes.INCIDENCIAS_NUMERO_DIAS_ID)
                        .setCellValue(incidencia.getNumeroDias() != null ? incidencia.getNumeroDias() : 0 );

                if (incidencia.getUnidadMedidaId() != null){
                    fila.createCell(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA_ID, CellType.NUMERIC)
                            .setCellValue(obtenUnidadMedida(incidencia.getUnidadMedidaId()));
                }

                if (incidencia.getNumeroHorasExtras() != null){
                    fila.createCell(ConstantesReportes.INCIDENCIAS_NUMERO_HORAS_EXTRAS_ID, CellType.NUMERIC)
                            .setCellValue(incidencia.getNumeroHorasExtras());
                }

                if (incidencia.getMonto() != null){
                    fila.createCell(ConstantesReportes.INCIDENCIAS_MONTO_ID, CellType.NUMERIC)
                            .setCellValue(incidencia.getMonto());
                }

                if (incidencia.getTipoIncapacidadId() != null){
                    fila.createCell(ConstantesReportes.INCIDENCIAS_TIPO_INCAPACIDAD_ID)
                            .setCellValue(obtenTipoIncapacidad(incidencia.getTipoIncapacidadId()));
                }

                XSSFCell celdaFechaInidencia = fila.createCell(ConstantesReportes.INCIDENCIAS_FECHA_APLICACION_ID);
                celdaFechaInidencia.setCellValue(incidencia.getFechaAplicacion());
                celdaFechaInidencia.setCellStyle(estiloCeldaFecha);
                XSSFCell celdaFechaInicio = fila.createCell(ConstantesReportes.INCIDENCIAS_FECHA_INICIO_ID);
                celdaFechaInicio.setCellValue(incidencia.getFechaInicio());
                celdaFechaInicio.setCellStyle(estiloCeldaFecha);
                fila.createCell(ConstantesReportes.INCIDENCIAS_ERRORES_ID).setCellValue(incidencia.getErrores());

            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                libro.write(bos);
            } finally {
                bos.close();
            }
            FileOutputStream file =
                    new FileOutputStream("C:\\Users\\Usuario\\Desktop\\ASG\\incidenciaErrores.xlsx");
            libro.write(file);
            return new RespuestaGenerica(bos.toByteArray(),Constantes.RESULTADO_EXITO,Constantes.EXITO);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" descargaEmleadosErroneos " + Constantes.ERROR_EXCEPCION, ex);
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

    private void creaCeldaError(XSSFSheet hoja, XSSFCellStyle estilo) {
        XSSFCell celdaError = hoja.getRow(0).createCell(ConstantesReportes.INCIDENCIAS_ERRORES_ID);
        celdaError.setCellValue("Errores");
        celdaError.setCellStyle(estilo);
    }

    private String obtenTipoIncidencia(Integer tipoIncidenciaId){
        String tipoIncidencia = "";
        for (CatTipoIncidenciaEnum value : CatTipoIncidenciaEnum.values()) {
            if (value.getId().equals(tipoIncidenciaId)){
                tipoIncidencia = value.getId() + "-" + value.getDescripcion();
            }
        }
        return tipoIncidencia;
    }

    private String obtenTipoIncapacidad(Integer tipoIncidenciaId) {
        Optional<CatTipoIncapacidad> catTipoIncapacidad = catTipoIncapacidadRepository.findById(tipoIncidenciaId);
        if (catTipoIncapacidad.isPresent()){
            return catTipoIncapacidad.get().getTipoIncapacidadId() + "-" + catTipoIncapacidad.get().getDescripcion();
        }else {
            return "";
        }
    }

    private String obtenUnidadMedida(Integer unidadMedidaId){
        Optional<CatUnidad> catUnidad = catUnidadRepository.findById(unidadMedidaId);
        if (catUnidad.isPresent()){
            return catUnidad.get().getUnidadMedidaId() + "-" + catUnidad.get().getDescripcion();
        }else {
            return "";
        }
    }

}
