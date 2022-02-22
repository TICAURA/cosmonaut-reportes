package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoIncapacidadRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoIncidenciaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;
import mx.com.ga.cosmonaut.reportes.services.CargaMasivaEventosIncidenciasService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class CargaMasivaEventosIncidenciasServiceImpl implements CargaMasivaEventosIncidenciasService {

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Inject
    private CatTipoIncidenciaRepository catTipoIncidenciaRepository;

    @Inject
    private CatTipoIncapacidadRepository catTipoIncapacidadRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica crearCargaMasivaEventosIncidencias(ReporteEventoIncidencia reporteEventoIncidencia) throws ServiceException {
        try (InputStream reporteJasper = this.getClass()
                .getResourceAsStream(RUTA_JRXML_EVENTOSINCIDENCIAS)){
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{"Eventos-Incidencias"};
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
                    RUTA_ARCHIVO_EVENTOSINCIDENCIAS) && cargarCatalogos(reporteEventoIncidencia)) {

                respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_EVENTOSINCIDENCIAS));
                respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearCargaMasivaEventosIncidencias "
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean cargarCatalogos(ReporteEventoIncidencia reporteEventoIncidencia) throws ServiceException{
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_EVENTOSINCIDENCIAS))) {

            List<String> listaIncidencias = catTipoIncidenciaRepository
                    .findByEsActivoAndEsIncidencia(reporteEventoIncidencia.getEsActivo());
            List<String> listaIncapacidad = catTipoIncapacidadRepository
                    .findByEsActivo(reporteEventoIncidencia.getEsActivo());

            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet realSheet = workbook.getSheetAt(0);

            DataValidationHelper validationHelper = new XSSFDataValidationHelper(realSheet);
            Row row = realSheet.createRow(1);

            List<List<?>> listaMaster = Arrays.asList(listaIncidencias, listaIncapacidad, LISTA_CAMPOS_UNIDAD);

            List<Integer> listaValorDefaul =
                    Arrays.asList(2, 9, 3);

            valorDefaulCelda(listaValorDefaul, row);

            guardarRegistroLista(workbook, listaMaster);
            generaFormulaLista(workbook, validationHelper, listaMaster,
                    listaValorDefaul,1);

            List<Integer> formato = Arrays.asList(0);
            formatoCelda(formato,workbook);
            List<Integer> formatoq= Arrays.asList(5);
            formatoCeldaNumerico(formatoq, workbook);
            List<Integer> formatoEntero= Arrays.asList(4);
            formatoCeldaNumericoEntero(formatoEntero, workbook);

            recuperarNumeroEmpleado(reporteEventoIncidencia,realSheet);
            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA + RUTA_ARCHIVO_EVENTOSINCIDENCIAS);
            workbook.write(stream);
            stream.close();

            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " cargarCatalogos "
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean recuperarNumeroEmpleado(ReporteEventoIncidencia reporteEventoIncidencia,
                                            XSSFSheet realSheet) throws ServiceException {
        try {
            List<NcoContratoColaborador> listaColaborador = ncoContratoColaboradorRepository
                    .findByEmpresa(reporteEventoIncidencia.getIdEmpresa());

            List<NcoContratoColaborador> listaNumeroColaborador;

            
            /** validacion de listaEmpleados */
            if (reporteEventoIncidencia.getListaIdEmpleados().isEmpty() ||
                    reporteEventoIncidencia.getListaIdEmpleados() == null) {
                listaNumeroColaborador = listaColaborador;
            } else {
                listaNumeroColaborador = listaColaborador
                        .stream()
                        .filter(c -> reporteEventoIncidencia
                                .getListaIdEmpleados()
                                .contains(c.getPersonaId().getPersonaId()))
                        .collect(Collectors.toList());
            }

            /**Escritura de numero Empleado en excel.*/
            AtomicInteger numCelda = new AtomicInteger(1);
            listaNumeroColaborador.stream().forEach(a -> {
                AtomicReference<XSSFRow> headerfilaTitulos =
                        new AtomicReference<>(realSheet.createRow(numCelda.get()));
                XSSFCell cell = headerfilaTitulos.get().createCell(0);
                cell.setCellValue(a.getNumEmpleado());
                XSSFCell celdaNombre = headerfilaTitulos.get().createCell(1);
                celdaNombre.setCellValue(a.getPersonaId().getNombre() + " " + a.getPersonaId().getApellidoPaterno());
                if (1 == numCelda.get()) {
                    headerfilaTitulos.get().createCell(2)
                            .setCellValue("Catálogo");
                    headerfilaTitulos.get().createCell(9)
                            .setCellValue("Catálogo");
                    headerfilaTitulos.get().createCell(3)
                            .setCellValue("Catálogo");
                }
                numCelda.getAndIncrement();
            });
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " recuperarNumeroEmpleado "
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }
}
