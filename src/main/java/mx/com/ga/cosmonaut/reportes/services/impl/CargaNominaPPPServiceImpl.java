package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.ColaboradorPagoComplementarioConsulta;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import mx.com.ga.cosmonaut.reportes.services.CargaNominaPPPService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class CargaNominaPPPServiceImpl implements CargaNominaPPPService {

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    @Override
    public RespuestaGenerica crearCargaMasivaNominaPPP(ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_NOMINAPPP)){
            HashMap<String, Object> parametros = new HashMap<>();
            JasperPrint reporte = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());
            String[] nombreHoja = new String[]{"Nomina PPP"};
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

            if (Boolean.TRUE.equals(crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_NOMINAPPP))
                && Boolean.TRUE.equals(abrirArchivoManipulacion(reporteIncidenciaModel))) {
                respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_NOMINAPPP));
                respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearCargaMasivaNominaPPP " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean abrirArchivoManipulacion(ReporteIncidenciaModel reporteIncidenciaModel) throws ServiceException{
        try (InputStream file = this.getClass().getResourceAsStream(RUTA_ARCHIVO_NOMINAPPP_WEB)){
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            escribirDatos(reporteIncidenciaModel,workbook);
            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA +
                    RUTA_ARCHIVO_NOMINAPPP);
            workbook.write(stream);
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " abrirArchivoManipulacion " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean escribirDatos(ReporteIncidenciaModel reporteIncidenciaModel,
                                  XSSFWorkbook workbook) throws ServiceException {
        try {
            /**consulta de datos
             * Se agrega cod_banco en el lugar de instituto en vez de nombre corto
             * */
            List<ColaboradorPagoComplementarioConsulta> complementarioConsultas =
                    empleadosReporteRepository
                            .colaboradorPagoComplementario(reporteIncidenciaModel.getIdEmpresa());

            complementarioConsultas = complementarioConsultas
                    .stream()
                    .filter(c -> reporteIncidenciaModel.getListaIdEmpleados()
                    .contains(c.getPersonaId())).collect(Collectors.toList());

            XSSFSheet hojaActual = workbook.getSheetAt(0);
            AtomicInteger i = new AtomicInteger(1);

            /** llenado de datos. */
            List<ColaboradorPagoComplementarioConsulta> finalComplementarioConsultas = complementarioConsultas;
            complementarioConsultas
                    .stream()
                    .forEach(a -> {
                        AtomicReference<XSSFRow> headerfilaTitulos =
                                new AtomicReference<>(hojaActual.createRow(i.get()));
                        for (int j=0;j<= 12;j++) {
                            XSSFCell cell = headerfilaTitulos.get().createCell(j);
                            if(j!=4)
                            cell.setCellValue(finalComplementarioConsultas.get(i.get()-1).getInfo(j+1));
                            if(j==8 || j==7 || j==5 || j==3 || j==6){
                                DataFormat fmt = workbook.createDataFormat();
                                CellStyle textStyle = workbook.createCellStyle();
                                textStyle.setDataFormat(fmt.getFormat("text"));
                                cell.setCellStyle(textStyle);
                            }
                            if(j==4){
                                DataFormat fmt = workbook.createDataFormat();
                                CellStyle textStyle = workbook.createCellStyle();
                                textStyle.setDataFormat((short)8);
                                cell.setCellStyle(textStyle);
                                String df=finalComplementarioConsultas.get(i.get()-1).getInfo(j+1);
                                Double d=Double.parseDouble(df);
                                String number= String.format("%.2f", d);
                                cell.setCellValue(d.doubleValue());
                                //cell.setCellValue(df);
                            }
                            //formatoCeldaNumerico(Arrays.asList(4), workbook);
                        }
                        i.getAndIncrement();
                    });

            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " escribirDatos " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }
}
