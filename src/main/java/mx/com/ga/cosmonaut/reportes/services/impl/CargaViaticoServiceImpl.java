package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.reportes.services.CargaViaticoService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.convertiraBase64;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.crearArchivo;

@Singleton
public class CargaViaticoServiceImpl implements CargaViaticoService {

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Override
    public RespuestaGenerica crearCargaMasivaViatico() throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_VIATICO)){
            HashMap<String, Object> parametros = new HashMap<>();
            JasperPrint reporte = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());
            String[] nombreHoja = new String[]{"Viaticos"};
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

            if (Boolean.TRUE.equals(crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_VIATICO))) {
                respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_VIATICO));
                respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearCargaMasivaViatico " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }
}
