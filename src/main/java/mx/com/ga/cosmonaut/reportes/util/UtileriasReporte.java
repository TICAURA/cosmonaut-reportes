package mx.com.ga.cosmonaut.reportes.util;

import mx.com.ga.cosmonaut.common.dto.DocumentosEmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.DocumentosEmpleadoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.NominaRespuesta;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;


public class UtileriasReporte {

    private static final String RUTA_CARPETA = mx.com.ga.cosmonaut.reportes.util.Constantes.RUTA_CARPETA;

    @Inject
    private final DocumentosEmpleadoRepository documentosEmpleadoRepository;

    private UtileriasReporte() {
        throw new IllegalStateException("Utility class");
    }

    public static String convertiraBase64(String rutaArchivo) throws ServiceException{
        String cadenaArchivo = "";
        try{
            File archiv = new File(RUTA_CARPETA + rutaArchivo);
            byte[] fileContent = Files.readAllBytes(archiv.toPath());
            cadenaArchivo = (Base64.getEncoder().encodeToString(fileContent));
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " convertiraBase64 " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
        return cadenaArchivo;
    }

    
    public static Boolean crearArchivo(JasperPrint reporte, SimpleXlsxReportConfiguration configuration,
                                 ByteArrayOutputStream byteArrayOutputStream, String rutaArchivo) throws ServiceException{

        File archivoSalida = new File(RUTA_CARPETA + rutaArchivo);
        try (ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
             OutputStream fileOutputStream = new FileOutputStream(archivoSalida)) {
            Exporter exporte = new JRXlsxExporter();
            exporte.setExporterInput(new SimpleExporterInput(reporte));
            exporte.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream2));
            exporte.setConfiguration(configuration);
            exporte.exportReport();
            byteArrayOutputStream.writeTo(fileOutputStream);
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearArchivo " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static Boolean valorDefaulCelda(List<Integer> listaCeldasValor, Row row) throws ServiceException{
        try {
            listaCeldasValor.forEach(c -> {
                Cell celda = row.createCell(c);
                celda.setCellValue("Catálogo");
            });
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " valorDefaulCelda " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static Boolean ocultarHoja(XSSFWorkbook workbook, XSSFSheet sheet, Integer hoja) throws ServiceException{
       try {
           workbook.setSheetHidden(hoja,true);
           sheet.enableLocking();
           return Boolean.TRUE;
       } catch (Exception ex) {
           throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                   UtileriasReporte.class.getSimpleName()
                   + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " ocultarHoja " +
                   mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
       }
    }

    public static void formatoCelda(List<Integer> listaCamposNumero, Workbook workbook) {
        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("@"));
        XSSFSheet sheetPrincipal = (XSSFSheet) workbook.getSheetAt(0);
        listaCamposNumero.forEach(c ->
            sheetPrincipal.setDefaultColumnStyle(c,textStyle));
    }

    public static void formatoCeldaText(List<Integer> listaCamposNumero, Workbook workbook) {
        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("text"));
        XSSFSheet sheetPrincipal = (XSSFSheet) workbook.getSheetAt(0);
        listaCamposNumero.forEach(c ->
                sheetPrincipal.setDefaultColumnStyle(c,textStyle));
    }

    public static void formatoCeldaNumerico(List<Integer> listaCamposNumero, Workbook workbook) {
        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("0.0"));
        XSSFSheet sheetPrincipal = (XSSFSheet) workbook.getSheetAt(0);
        listaCamposNumero.forEach(c ->
                sheetPrincipal.setDefaultColumnStyle(c,textStyle));
    }

    public static void formatoCeldaNumericoEntero(List<Integer> listaCamposNumero, Workbook workbook) {
        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("0"));
        XSSFSheet sheetPrincipal = (XSSFSheet) workbook.getSheetAt(0);
        listaCamposNumero.forEach(c ->
                sheetPrincipal.setDefaultColumnStyle(c,textStyle));
    }

    public static Boolean guardarRegistroLista(XSSFWorkbook workbook, List<List<?>> listaMaster) throws ServiceException{
        try {
            workbook.createSheet("hidden");
            XSSFSheet realSheet = workbook.getSheetAt(1);
            AtomicInteger valorMaximo = new AtomicInteger(0);
            listaMaster.forEach(c -> {
                if (c.size() > valorMaximo.get()) {
                    valorMaximo.set(c.size());
                }
            });
            IntStream.range(0,valorMaximo.get()).forEach(index -> {
                XSSFRow headerRow = realSheet.createRow(index);
                for (int i = 0; i < listaMaster.size(); i ++){
                    if (listaMaster.get(i).size() > index) {
                        String valorLista = (String) (listaMaster.get(i).get(index));
                        XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(valorLista);
                    }
                }
            });
            ocultarHoja(workbook,realSheet, 1);
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " guardarRegistroLista " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static Boolean generaFormulaLista(XSSFWorkbook workbook, DataValidationHelper validationHelper,
                                          List<List<?>> listaMaster, List<Integer> listaCampos,Integer inicioRow) throws ServiceException{
        try {
            for (int i = 0; i < listaMaster.size(); i++) {
                char x = 'A';
                x += i;
                XSSFSheet sheetPrincipal = workbook.getSheetAt(0);
                Name namedCell = workbook.createName();
                namedCell.setNameName("hidden" + x);
                int tamanio;
                if(listaMaster.get(i).size() != 0){
                    tamanio = listaMaster.get(i).size();
                }else{
                    tamanio = 1;
                }

                namedCell.setRefersToFormula("hidden!$" + x + "$1:$" + x + "$" + tamanio );
                DataValidationConstraint constraint = validationHelper.createFormulaListConstraint("hidden" + x);
                CellRangeAddressList addressList =
                        new CellRangeAddressList(inicioRow, 1000, listaCampos.get(i),
                                listaCampos.get(i));
                DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
                sheetPrincipal.addValidationData(dataValidation);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " generaFormulaLista " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static Boolean validaCadena(String cadena) {
        return (cadena != null && !cadena.isEmpty() && !cadena.equalsIgnoreCase("null")
        ? Boolean.TRUE : Boolean.FALSE);
    }

    public static void compilarReporteJasper(String rutaJRXML, String nombreHojas) throws ServiceException{
        try (InputStream reporteJasper = UtileriasReporte.class.getResourceAsStream(rutaJRXML);) {
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{nombreHojas};
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
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " compilarReporteJasper " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static XSSFCellStyle crearEstiloCeldas(XSSFWorkbook workbook) {
        XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
        XSSFFont fuente = workbook.createFont();
        fuente.setBold(true);
        /**color*/
        XSSFColor color = new XSSFColor(new java.awt.Color(37,222,212),null);
        xssfCellStyle.setFillForegroundColor(color);
        xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        xssfCellStyle.setLocked(true);
        xssfCellStyle.setWrapText(true);
        xssfCellStyle.setFont(fuente);
        return xssfCellStyle;
    }

    public static void unirCeldas(XSSFSheet hojaActual, Integer firstRow, Integer lastRow,
                                  Integer firstCol, Integer lastCol) {
        hojaActual.addMergedRegion(new CellRangeAddress(firstRow,lastRow,firstCol,lastCol));
    }

    public static RespuestaGenerica generaRespuesta(NominaRespuesta nominaRespuesta) {
        if (nominaRespuesta.getResponseDetail().getCode() >= 0){
            return new RespuestaGenerica(nominaRespuesta.getResponse(), mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO, mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
        } else {
            return new RespuestaGenerica(nominaRespuesta.getResponse(), mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_ERROR,nominaRespuesta.getResponseDetail().getMessage());
        }
    }

    public static String formatoFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date d = formato.parse(fecha);

        formato.applyPattern("dd-MM-yyyy");
        return formato.format(d);

    }

    public static String validaCadenaComplementaria(String valor, Integer longitud, Integer lado, Integer ceros) throws ServiceException{
        try {
            valor = validaContenido(valor);

            if (valor.length() > longitud) {
                valor = valor.substring(0,longitud);
            }

            if (valor.length() < longitud) {

                /** lado derecho.*/
                if (lado == 1) {
                    valor = String.format("%-".concat(String.valueOf(longitud).concat("s")) , valor);
                } else { /**lado izquierdo*/
                    valor = String.format("%".concat(longitud.toString()).concat("s"), valor);
                }
                if (ceros == 1) {
                    valor = valor.replace(" ", "0");
                }

            }
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " validaCadena " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
        return valor;
    }

    public static String validaContenido(String valor) throws ServiceException {
        try {
            return (Boolean.TRUE.equals(validaCadena(valor)) ? valor : "");
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " validaContenido " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static String validaDato(Object cadena) throws ServiceException {
        try {
            if (cadena != null) {
                return (validaContenido(cadena.toString()));
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " validaContenido " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static XSSFCellStyle crearEstiloBorde(XSSFWorkbook workbook) throws ServiceException {
        try{
            XSSFCellStyle estilo = workbook.createCellStyle();
            XSSFColor color = new XSSFColor(new java.awt.Color(37,222,212),null);


            estilo.setBottomBorderColor(color);
            estilo.setTopBorderColor(color);
            estilo.setBorderTop(BorderStyle.MEDIUM);
            estilo.setBorderBottom(BorderStyle.MEDIUM);
            return estilo;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" crearEstiloBorde " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static XSSFCellStyle crearEstiloBordeEspecial(XSSFWorkbook workbook) throws ServiceException {
        try{
            XSSFCellStyle estilo = workbook.createCellStyle();
            XSSFColor color = new XSSFColor(new java.awt.Color(37,222,212),null);

            XSSFFont fuente = workbook.createFont();
            fuente.setBold(true);

            estilo.setBottomBorderColor(color);
            estilo.setTopBorderColor(color);
            estilo.setBorderTop(BorderStyle.MEDIUM);
            estilo.setBorderBottom(BorderStyle.MEDIUM);
            estilo.setAlignment(HorizontalAlignment.RIGHT);
            estilo.setFont(fuente);
            return estilo;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" crearEstiloBorde " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static void crearCeldaBorde(Integer numCelda, XSSFWorkbook workbook, AtomicReference<XSSFRow> fila,
                            String valorCelda) throws ServiceException {
        try {
            Cell cell = fila.get().createCell(numCelda);
            cell.setCellValue(valorCelda);
            cell.setCellStyle(crearEstiloBorde(workbook));
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" crearCeldaBorde " + Constantes.ERROR_EXCEPCION, ex);
        }
    }
    public static void crearCeldaBordeEspecial(Integer numCelda, XSSFWorkbook workbook, AtomicReference<XSSFRow> fila,
                                       String valorCelda) throws ServiceException {
        try {
            Cell cell = fila.get().createCell(numCelda);
            cell.setCellValue(valorCelda);
            cell.setCellStyle(crearEstiloBordeEspecial(workbook));
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" crearCeldaBorde " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static Boolean borrarArchivo(String nombreArchivo) throws ServiceException {
        try {
            File archivo = new File(RUTA_CARPETA + nombreArchivo);
            if (archivo.exists()){
                Files.delete(archivo.toPath());
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" borrarArchivo " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static void crearArchivoXml(String xmlStr, String nombreArchivo) throws ServiceException {
        try {
            Document doc = convertStringAXML(xmlStr);
            String realPath = RUTA_CARPETA + nombreArchivo;
            File file = new File(realPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(realPath));
            transformer.transform(source, result);
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO + " crearArchivoXml " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private static Document convertStringAXML(String xmlString) throws ServiceException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO + " convertStringToXMLDocument " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public static Boolean validaContenidoLista(List<?> lista) {
        return lista != null && !lista.isEmpty();
    }

    public static void crearTitulosListasDinamicas(List<String> listaValores,
                                             AtomicReference<XSSFRow> headerfila, AtomicInteger i,
                                             XSSFWorkbook workbook, XSSFSheet hojaActual) throws ServiceException{
        try {
            if (Boolean.TRUE.equals(validaContenidoLista(listaValores))) {
                listaValores
                        .stream()
                        .forEach(a -> {
                            XSSFCell cell = headerfila.get().createCell(i.get());
                            cell.setCellValue(a);
                            cell.setCellStyle(crearEstiloCeldas(workbook));
                            hojaActual.setColumnWidth(i.get(), 8000);
                            i.getAndIncrement();
                        });
            }
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    UtileriasReporte.class.getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearTitulosListasDinamicas " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static DocumentosEmpleadoDto documentoEmpleado(String nombreArchivo, String cadena64, Integer personaId,
                                                    Integer centrocCliente, Integer tipoMultimedia, Integer tipoDocumentoId) throws ServiceException {
        try {
            DocumentosEmpleadoDto documentosEmpleadoDto = new DocumentosEmpleadoDto();

            documentosEmpleadoDto.setCentrocClienteId(centrocCliente);
            documentosEmpleadoDto.setDocumento(Utilidades.decodeContent(cadena64));
            documentosEmpleadoDto.setPersonaId(personaId);
            documentosEmpleadoDto.setNombreArchivo(nombreArchivo);
            documentosEmpleadoDto.setTipoDocumentoId(tipoDocumentoId);
            documentosEmpleadoDto.setCmsTipoMultimedia(tipoMultimedia);
            documentosEmpleadoDto.setUsuarioId(1);
            return documentosEmpleadoDto;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + UtileriasReporte.class.getSimpleName()
                    + Constantes.ERROR_METODO +" documentoEmpleado " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static String getFechaConversion(String fecha) throws ParseException {
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
        Date date = format.parse(fecha);

        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    public static String redondeoCantidad(String cantidad, int redondeo) {
        return new BigDecimal(cantidad)
                .setScale(redondeo, RoundingMode.HALF_UP).toString();
    }
}
