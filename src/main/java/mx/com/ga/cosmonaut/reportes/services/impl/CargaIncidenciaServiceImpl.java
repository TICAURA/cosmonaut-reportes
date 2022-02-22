package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoIncapacidadRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import mx.com.ga.cosmonaut.reportes.services.CargaIncidenciaService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class CargaIncidenciaServiceImpl implements CargaIncidenciaService {

    private static final Logger LOGG = LoggerFactory.getLogger(CargaIncidenciaServiceImpl.class);

    private RespuestaGenerica respuesta = new RespuestaGenerica();

    @Inject
    private CatTipoIncapacidadRepository catTipoIncapacidadRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica crearReporteIncidencia(ReporteIncidenciaModel incidenciaModel) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_INCIDENCIA)){
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{"Incidencia"};
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(true);
            configuration.setIgnoreGraphics(false);
            configuration.setWhitePageBackground(false);
            configuration.setRemoveEmptySpaceBetweenRows(true);
            configuration.setSheetNames(nombreHoja);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(reporte));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            if (crearArchivo(reporte, configuration, byteArrayOutputStream,
                    RUTA_ARCHIVO_INCIDENCIA) && abrirArchivoManipulacion(incidenciaModel)) {
                    respuesta.setDatos(convertiraBase64(RUTA_ARCHIVO_INCIDENCIA));
                    respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
                }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearReporteIncidencia "
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean abrirArchivoManipulacion(ReporteIncidenciaModel incidenciaModel) {
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_INCIDENCIA))){
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet realSheet = workbook.getSheetAt(0);
            modificarValorCeldasLista(realSheet, workbook, incidenciaModel);
            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA +
                    RUTA_ARCHIVO_INCIDENCIA);
            workbook.write(stream);
            stream.close();
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGG.error("Error en abrirArchivoManipulacion ->", e);
            return Boolean.FALSE;
        }
    }

    private Boolean modificarValorCeldasLista(XSSFSheet realSheet, XSSFWorkbook workbook,
                                              ReporteIncidenciaModel incidenciaModel) {
        try {
            DataValidationHelper validationHelper = new XSSFDataValidationHelper(realSheet);

            if (!incidenciaModel.getListaIdEmpleados().isEmpty() ||
            incidenciaModel.getIdEmpresa() != null) {

                List<List<?>> listaMaster;
                List<String> listaTipoIncidencia = catTipoIncapacidadRepository.findByEsActivoOrderByDescripcion(Boolean.TRUE)
                        .stream()
                        .map(c -> c.getTipoIncapacidadId() + SEPARADOR + c.getDescripcion())
                        .collect(Collectors.toList());

                List<NcoPersona> listaEmpleados = ncoContratoColaboradorRepository.findByEmpresaEmpleado(incidenciaModel.getIdEmpresa())
                        .stream()
                        .filter(empleado -> incidenciaModel.getListaIdEmpleados().contains(empleado.getPersonaId()))
                        .collect(Collectors.toList());

                listaMaster = Arrays.asList(LISTA_INCIDENCIA_HORAS_EXTRAS, listaTipoIncidencia);
                guardarRegistroLista(workbook, listaMaster);
                generaFormulaLista(workbook, validationHelper, listaMaster, LISTA_CELDA_VALOR_INCIDENCIA,2);
                crearDatosEmpleado(workbook, listaEmpleados);
            }
            realSheet.createFreezePane(0,2);
            return Boolean.TRUE;
        } catch (NullPointerException | ServiceException e) {
            LOGG.error("Error en modificarValorCeldasLista ->",e);
            return Boolean.FALSE;
        }
    }

    private Boolean crearDatosEmpleado (XSSFWorkbook workbook, List<NcoPersona> listaEmpleados) {
        try {
            XSSFSheet realSheet = workbook.getSheetAt(0);
            XSSFRow dataRow;

            for (int i = 0; i < listaEmpleados.size(); i ++) {
                dataRow = realSheet.createRow(i + 2);
                Integer idEmpleado = listaEmpleados.get(i).getPersonaId();
                String nombre = (Boolean.TRUE.equals(validaCadena(listaEmpleados.get(i).getNombre())) ?
                        listaEmpleados.get(i).getNombre() : "");
                String apellidoPaterno = Boolean.TRUE.equals(validaCadena(listaEmpleados.get(i).getApellidoPaterno())) ?
                        listaEmpleados.get(i).getApellidoPaterno() : "";
                String apellidoMaterno = Boolean.TRUE.equals(validaCadena(listaEmpleados.get(i).getApellidoMaterno())) ?
                        listaEmpleados.get(i).getApellidoMaterno() : "";

                CellStyle unlockedCellStyle = workbook.createCellStyle();
                unlockedCellStyle.setLocked(false);
                unlockedCellStyle.setHidden(true);

                Cell cell = dataRow.createCell(0);
                cell.setCellValue(idEmpleado);
                cell.setCellStyle(unlockedCellStyle);

                dataRow.createCell(0).setCellValue(idEmpleado);
                dataRow.createCell(1).setCellValue(nombre);
                dataRow.createCell(2).setCellValue(apellidoPaterno);
                dataRow.createCell(3).setCellValue(apellidoMaterno);
            }

            return Boolean.TRUE;
        } catch (Exception e) {
            LOGG.error("Error en crearDatosEmpleado -> ",e);
            return Boolean.FALSE;
        }
    }

}
