package mx.com.ga.cosmonaut.reportes.services.impl;

import io.micronaut.http.multipart.CompletedFileUpload;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.confronta.FiltradoRequest;
import mx.com.ga.cosmonaut.common.dto.confronta.ZipTextFileDto;
import mx.com.ga.cosmonaut.common.entity.confronta.CftConfronta;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.confronta.CftConfrontaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.ConfrontaRepository;
import mx.com.ga.cosmonaut.reportes.services.ConfrontaService;
import mx.com.ga.cosmonaut.reportes.util.Constantes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Singleton
public class ConfrontaServiceImpl implements ConfrontaService {

    @Inject
    private CftConfrontaRepository cftConfrontaRepository;

    @Inject
    private ConfrontaRepository confrontaRepository;

    @Override
    public RespuestaGenerica guardar(/*GuardarRequest request, */CompletedFileUpload file) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            /*CftConfronta cftConfronta = new CftConfronta();
            cftConfronta.setRegistroPadronal(request.getRegistroPadronal());
            cftConfronta.setRazonSocial(request.getRazonSocial());
            cftConfronta.setAnio(request.getAnio());
            cftConfronta.setMes(request.getMes());
            cftConfronta.setEmision(catEmisionRepository.findById(request.getEmision()));
            cftConfronta.setReferenciaDoc("?");
            cftConfronta.setEsActivo(mx.com.ga.cosmonaut.common.util.Constantes.ESTATUS_ACTIVO);*/

            if (file.getContentType().isPresent() && validarZip(file.getContentType().get().toString())) {
                List<ZipTextFileDto> textFiles = leerTxts(file.getBytes());
                if (textFiles.isEmpty() || !ejecutarConfronta(textFiles)) {
                    respuesta.setResultado(false);
                    respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.ERROR);
                } else {
                    //respuesta.setDatos(cftConfrontaRepository.save(cftConfronta));
                    respuesta.setResultado(true);
                    respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
                }
            } else {
                respuesta.setResultado(false);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE
                    + this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " guardar "
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validarZip(String contentType) {
        return contentType.equals("application/zip");
    }

    private List<ZipTextFileDto> leerTxts(byte[] file) {
        try (InputStream input = new ByteArrayInputStream(file);
             ZipInputStream zip = new ZipInputStream(input)) {

            List<ZipTextFileDto> txtFiles = new ArrayList<>();

            ZipEntry entry;
            Scanner sc = null;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".txt")) {
                    sc = new Scanner(zip);
                    ZipTextFileDto textFile = new ZipTextFileDto();

                    while (sc.hasNextLine()) {
                        textFile.getContenido().add(sc.nextLine());
                    }

                    textFile.setNombre(entry.getName());
                    txtFiles.add(textFile);
                }
            }
            if (sc != null) {
                sc.close();
            }

            return txtFiles;
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean ejecutarConfronta(List<ZipTextFileDto> textFiles) throws IOException, ServiceException {
        generarLibro();
        return true;
    }

    private void generarLibro() throws IOException, ServiceException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<String> hojas = Arrays.asList(Constantes.CFT_TRAB_SHEET, Constantes.CFT_MOV_SHEET,
                Constantes.CFT_NSS_SHEET, Constantes.CFT_SDI_SHEET, Constantes.CFT_NOM_SHEET,
                Constantes.CFT_AMOR_SHEET, Constantes.CFT_RAMOR_SHEET, Constantes.CFT_SDIP_SHEET);

        for (String hoja : hojas) {
            generarHoja(workbook, hoja);
        }

        FileOutputStream outputStream = new FileOutputStream("C:\\Temp\\MyFirstExcel.xlsx");
        workbook.write(outputStream);
        workbook.close();
    }

    private void generarHoja(XSSFWorkbook workbook, String hoja) throws ServiceException {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short)10);
        font.setBold(true);

        byte[] turq = new byte[]{(byte)37, (byte)222, (byte)212};
        byte[] aq = new byte[]{(byte)204, (byte)255, (byte)255};
        byte[] green = new byte[]{(byte)198, (byte)224, (byte)180};

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setFont(font);

        XSSFCellStyle styleSubtitle = workbook.createCellStyle();
        styleSubtitle.setFont(font);

        XSSFCellStyle styleTurq = workbook.createCellStyle();
        styleTurq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleTurq.setFillForegroundColor(new XSSFColor(turq, null));
        styleTurq.setAlignment(HorizontalAlignment.CENTER);
        styleTurq.setFont(font);

        XSSFCellStyle styleAqua = workbook.createCellStyle();
        styleAqua.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleAqua.setFillForegroundColor(new XSSFColor(aq, null));
        styleAqua.setAlignment(HorizontalAlignment.CENTER);
        styleAqua.setFont(font);

        XSSFCellStyle styleGreen = workbook.createCellStyle();
        styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleGreen.setFillForegroundColor(new XSSFColor(green, null));
        styleGreen.setAlignment(HorizontalAlignment.CENTER);
        styleGreen.setFont(font);

        String titulo = "";
        String subtitulo = null;
        List<String> headers = new ArrayList<>();
        List<String> headersComunes = new ArrayList<>(Arrays.asList(Constantes.CFT_REGPAT_CELL, Constantes.CFT_IDCTE_CELL,
                Constantes.CFT_CTE_CELL, Constantes.CFT_NOEMP_CELL, Constantes.CFT_NSS_CELL, Constantes.CFT_NOMTRAB_CELL));
        List<String> headersEmision = new ArrayList<>(Arrays.asList(Constantes.CFT_REGPAT_CELL, Constantes.CFT_NSS_CELL,
                Constantes.CFT_NOMTRAB_CELL));
        List<String> headersDiferencia = null;

        XSSFSheet sheet = workbook.createSheet(hoja);
        switch (hoja) {
            case Constantes.CFT_TRAB_SHEET: {
                titulo = Constantes.CFT_TRAB_TITLE;
                subtitulo = Constantes.CFT_TRAB_SUB1;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_MOV_CELL, Constantes.CFT_FECHAMOV_CELL,
                        Constantes.CFT_SDI_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision.addAll(headersEspecificos);
                break;
            }
            case Constantes.CFT_MOV_SHEET: {
                titulo = Constantes.CFT_MOV_TITLE;
                subtitulo = Constantes.CFT_MOV_SUB1;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_MOV_CELL, Constantes.CFT_FECHAMOV_CELL,
                        Constantes.CFT_SDI_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision.addAll(headersEspecificos);
                break;
            }
            case Constantes.CFT_NSS_SHEET:
                titulo = Constantes.CFT_NSS_TITLE;
                break;
            case Constantes.CFT_SDI_SHEET: {
                titulo = Constantes.CFT_SDI_TITLE;
                subtitulo = Constantes.CFT_SDI_SUB1;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_MOV_CELL, Constantes.CFT_FECHAMOV_CELL,
                        Constantes.CFT_SDI_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision = new ArrayList<>(headersEspecificos);
                headersEmision.add(Constantes.CFT_DIF_CELL);
                break;
            }
            case Constantes.CFT_NOM_SHEET:
                titulo = Constantes.CFT_NOM_TITLE;
                headersEmision = Collections.singletonList(Constantes.CFT_NOMTRAB_CELL);
                break;
            case Constantes.CFT_AMOR_SHEET: {
                titulo = Constantes.CFT_AMOR_TITLE;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_NCRE_CELL, Constantes.CFT_FECHAIN_CELL,
                        Constantes.CFT_TIPODES_CELL, Constantes.CFT_DIAS_CELL, Constantes.CFT_INC_CELL,
                        Constantes.CFT_AUS_CELL, Constantes.CFT_FACTOR_CELL, Constantes.CFT_IMPORTE_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision = Arrays.asList(Constantes.CFT_NCRE_CELL, Constantes.CFT_FECHAIN_CELL,
                        Constantes.CFT_TIPODES_CELL, Constantes.CFT_DIAS_CELL, Constantes.CFT_FACTOR_CELL,
                        Constantes.CFT_IMPORTE_CELL);
                headersDiferencia = new ArrayList<>(headersEmision);
                headersDiferencia.addAll(Arrays.asList(Constantes.CFT_DIAS_CELL, Constantes.CFT_INC_CELL,
                        Constantes.CFT_AUS_CELL, Constantes.CFT_FACTOR_CELL));
                break;
            }
            case Constantes.CFT_RAMOR_SHEET: {
                titulo = Constantes.CFT_RAMOR_TITLE;
                subtitulo = Constantes.CFT_RAMOR_SUB1;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_NCRE_CELL, Constantes.CFT_FECHAIN_CELL,
                        Constantes.CFT_TIPODES_CELL, Constantes.CFT_DIAS_CELL, Constantes.CFT_INC_CELL,
                        Constantes.CFT_AUS_CELL, Constantes.CFT_FACTOR_CELL, Constantes.CFT_IMPORTE_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision = Arrays.asList(Constantes.CFT_NCRE_CELL, Constantes.CFT_FECHAIN_CELL,
                        Constantes.CFT_TIPODES_CELL, Constantes.CFT_DIAS_CELL, Constantes.CFT_FACTOR_CELL,
                        Constantes.CFT_IMPORTE_CELL);
                break;
            }
            case Constantes.CFT_SDIP_SHEET: {
                titulo = Constantes.CFT_SDIP_TITLE;
                subtitulo = Constantes.CFT_SDI_SUB1;
                List<String> headersEspecificos = Arrays.asList(Constantes.CFT_MOV_CELL, Constantes.CFT_FECHAMOV_CELL,
                        Constantes.CFT_SDI_CELL);
                headersComunes.addAll(headersEspecificos);
                headersEmision = headersEspecificos;
                break;
            }
        }

        headers.addAll(headersComunes);
        headers.addAll(headersEmision);
        if (headersDiferencia != null) {
            headers.addAll(headersDiferencia);
        }

        int rowCount = 0;
        XSSFRow row = sheet.createRow(rowCount);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(titulo);
        cell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount,0,headers.size()-1));

        if (subtitulo != null) {
            row = sheet.createRow(++rowCount);
            cell = row.createCell(0);
            cell.setCellValue(subtitulo);
            cell.setCellStyle(styleSubtitle);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount,0,headers.size()-1));
        }

        row = sheet.createRow(++rowCount);
        cell = row.createCell(0);
        cell.setCellValue("Cosmonaut");
        cell.setCellStyle(styleTurq);
        sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount,0,headersComunes.size()-1));

        cell = row.createCell(headersComunes.size());
        cell.setCellValue("Emisión");
        cell.setCellStyle(styleAqua);
        if (headersEmision.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(
                    rowCount, rowCount, headersComunes.size(), (headersComunes.size() + headersEmision.size() - 1)));
        }

        if (headersDiferencia != null) {
            cell = row.createCell(headersComunes.size() + headersEmision.size());
            cell.setCellValue("Diferencia");
            cell.setCellStyle(styleGreen);
            sheet.addMergedRegion(new CellRangeAddress(
                    rowCount, rowCount, (headersComunes.size() + headersEmision.size()), headers.size()-1));
        }

        row = sheet.createRow(++rowCount);
        int colum = 0;
        for (String header : headers) {
            cell = row.createCell(colum);
            cell.setCellValue(header);
            if (colum < headersComunes.size()) {
                cell.setCellStyle(styleTurq);
            } else if (colum < (headersComunes.size()+headersEmision.size())) {
                cell.setCellStyle(styleAqua);
            } else {
                cell.setCellStyle(styleGreen);
            }
            sheet.setColumnWidth(colum, 6000);
            colum++;
        }

        rellenar(workbook, hoja, ++rowCount);
    }

    private void rellenar(XSSFWorkbook workbook, String hoja, int rowCont) throws ServiceException {
        /*XSSFSheet sheet = workbook.getSheet(hoja);
        XSSFRow row = sheet.createRow(rowCont);*/
        confrontaRepository.obtieneDatosBase();
    }

    @Override
    public RespuestaGenerica filtrar(FiltradoRequest request) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(confrontaRepository.filtrar(request));
            respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +" filtrar" + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica descargar(Long id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            Optional<CftConfronta> confronta = cftConfrontaRepository.findById(id);
            if (confronta.isPresent()) {
                // LOGICA DE DESCARGA

                respuesta.setDatos(confronta.get());
                respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            } else {
                respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.REGISTRO_EXISTE);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +" filtrar" + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

}
