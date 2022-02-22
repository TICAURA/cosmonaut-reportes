package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import mx.com.ga.cosmonaut.reportes.services.CargaPTUService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.convertiraBase64;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.crearArchivo;

@Singleton
public class CargaPTUServiceImpl implements CargaPTUService {

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Inject
    private NcoPersonaRepository personaRepository;

    @Override
    public RespuestaGenerica crearCargaMasivaPTU(ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_PTU)){
            HashMap<String, Object> parametros = new HashMap<>();
            JasperPrint reporte = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());
            String[] nombreHoja = new String[]{"PTU"};
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

            if (Boolean.TRUE.equals(crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_PTU))
            && Boolean.TRUE.equals(escribirDatosPTU(reporteIncidenciaModel))) {
                respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_PTU));
                respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearCargaMasivaPTU " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean escribirDatosPTU(ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException{
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_PTU));
             XSSFWorkbook workbook = new XSSFWorkbook(file);
             FileOutputStream outFile = new FileOutputStream(
                     new File(RUTA_CARPETA + RUTA_ARCHIVO_PTU))) {

            List<NcoPersona> listaCargaPTU  =
                    personaRepository.findByPersona(reporteIncidenciaModel.getIdEmpresa());

            listaCargaPTU = listaCargaPTU
                    .stream()
                    .filter(c -> reporteIncidenciaModel
                            .getListaIdEmpleados()
                            .contains(c.getPersonaId()))
                    .collect(Collectors.toList());

            XSSFSheet hojaActual = workbook.getSheetAt(0);
            hojaActual.protectSheet("asg");
            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(1));
            AtomicInteger contEmpleado = new AtomicInteger(0);

            listaCargaPTU
                    .stream()
                    .forEach(c -> {
                        headerfila.set(hojaActual.createRow(contEmpleado.get() + 1));
                        crearCell(c.getCurp(),headerfila,0);
                        crearCell(c.getNombre(),headerfila,1);
                        crearCell(c.getApellidoPaterno(),headerfila,2);
                        crearCell(c.getApellidoMaterno(),headerfila,3);
                        crearCellStyle(headerfila,4, workbook);
                        crearCellStyle(headerfila,5, workbook);
                        contEmpleado.getAndIncrement();
                    });
            workbook.write(outFile);
            return true;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " escribirDatosPTU " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private void crearCell(String c,AtomicReference<XSSFRow> headerfila, Integer numCell) {
        XSSFCell cell = headerfila.get()
                .createCell(numCell);
        cell.setCellValue(c);
    }

    private void crearCellStyle(AtomicReference<XSSFRow> headerfila, Integer numCell,
                                         XSSFWorkbook wb) {
        XSSFCell cell = headerfila.get()
                .createCell(numCell);
        cell.setCellValue("");
        XSSFCellStyle unlockedCellStyle = wb.createCellStyle();
        unlockedCellStyle.setLocked(false);
        cell.setCellStyle(unlockedCellStyle);
    }
}
