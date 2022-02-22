package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatMetodoPagoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoCompensacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsBancoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoContratoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoRegimenContratacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatAreaGeograficaRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatEstadoRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.reportes.services.CargaExEmpleadoService;
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
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.formatoCelda;

public class CargaExEmpleadoServiceImpl implements CargaExEmpleadoService {

    private static final Logger LOGG = LoggerFactory.getLogger(CargaExEmpleadoServiceImpl.class);

    private RespuestaGenerica respuesta = new RespuestaGenerica();

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private CsTipoContratoRepository csTipoContratoRepository;

    @Inject
    private CatAreaGeograficaRepository catAreaGeograficaRepository;

    @Inject
    private CatTipoCompensacionRepository catTipoCompensacionRepository;

    @Inject
    private CatMetodoPagoRepository catMetodoPagoRepository;

    @Inject
    private CsBancoRepository csBancoRepository;

    @Inject
    private CatEstadoRepository catEstadoRepository;

    @Inject
    private CsTipoRegimenContratacionRepository csTipoRegimenContratacionRepository;


    @Override
    public RespuestaGenerica crearReporteExEmpleado(Integer idEmpresa) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_EXEMPLEADO)){
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{"DatosExEmpleados"};
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
                    RUTA_ARCHIVO_EXEMPLEADO) && agregarListas(idEmpresa)) {
                respuesta.setDatos(convertiraBase64(RUTA_ARCHIVO_EXEMPLEADO));
                respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearReporteExEmpleado " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean agregarListas (Integer idEmpresa) {
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_EXEMPLEADO))){
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet realSheet = workbook.getSheetAt(0);

            crearListasEnCampos(realSheet, workbook,idEmpresa);

            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA + RUTA_ARCHIVO_EXEMPLEADO);
            workbook.write(stream);
            stream.close();
            return Boolean.TRUE;
        } catch (IOException | ServiceException e) {
            LOGG.error("Se genero un error en agregarListas -> ",e);
            return Boolean.FALSE;
        }
    }

    private void crearListasEnCampos(XSSFSheet realSheet, XSSFWorkbook workbook, Integer idEmpresa)
            throws ServiceException {
        try {
            List<String> empresaPolitica = ncoContratoColaboradorRepository.findByEmpresaPolitica(idEmpresa)
                    .stream()
                    .map(c -> c.getPoliticaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            List<String> empresaJornada = ncoContratoColaboradorRepository.findByEmpresaJornada(idEmpresa)
                    .stream()
                    .map(c -> c.getJornadaId() + SEPARADOR +
                            c.getTipoJornadaId().getTipoJornadaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            List<String> empresaGrupoNomina = ncoContratoColaboradorRepository.findByEmpresaGrupoNomina(idEmpresa)
                    .stream()
                    .map(c -> c.getGrupoNominaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            List<String> empresaArea = ncoContratoColaboradorRepository.findByEmpresaArea(idEmpresa)
                    .stream()
                    .map(c -> c.getAreaId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            List<String> empresaPuesto = ncoContratoColaboradorRepository.findByEmpresaPuesto(idEmpresa)
                    .stream()
                    .map(c -> c.getPuestoId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            DataValidationHelper validationHelper = new XSSFDataValidationHelper(realSheet);
            Row row = realSheet.createRow(2);

            List<String> tipoContrato = csTipoContratoRepository.findByEsActivo(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getTipoContratoId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            List<String> catAreaGeografica = catAreaGeograficaRepository.findByEsActivoOrderByDescripcion(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getAreaGeograficaId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            List<String> catTipoCompensacion = catTipoCompensacionRepository.findByEsActivoOrderByDescripcion(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getTipoCompensacionId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            List<String> catMetodoPago = catMetodoPagoRepository.findByEsActivoOrderByDescripcion(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getMetodoPagoId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            List<String> csBanco = csBancoRepository.findByEsActivoOrderByBancoId(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getBancoId() + SEPARADOR + c.getNombreCorto().trim())
                    .collect(Collectors.toList());

            List<String> catEstado = catEstadoRepository.findByEsActivoOrderByEstado(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getEstadoId() + SEPARADOR + c.getEstado().trim())
                    .collect(Collectors.toList());

            List<String> csTipoRegimen = csTipoRegimenContratacionRepository.findByEsActivo(Boolean.TRUE)
                    .stream()
                    .map(c -> c.getTipoRegimenContratacionId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());


            List<List<?>> listaMaster;
            /** Nuevo cambio*/
            listaMaster = Arrays.asList(
                    SEXO,
                    empresaArea,
                    empresaPuesto,
                    tipoContrato,
                    catEstado,
                    csTipoRegimen,
                    empresaPolitica,
                    DECISION,
                    catAreaGeografica,
                    empresaJornada,
                    catTipoCompensacion,
                    empresaGrupoNomina,
                    catMetodoPago,
                    csBanco);

            guardarRegistroLista(workbook, listaMaster);
            valorDefaulCelda(LISTA_CELDA_VALOR_EXEMPLEADO, row);
            generaFormulaLista(workbook, validationHelper, listaMaster,
                    LISTA_CELDA_VALOR_EXEMPLEADO,2);
            formatoCelda(LISTA_CAMPOS_NUMEROS_EXEMPLEADO, workbook);
            realSheet.createFreezePane(0,2);
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearListasEnCampos " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }


}
