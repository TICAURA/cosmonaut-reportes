package mx.com.ga.cosmonaut.reportes.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.EmpleadosReporte;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatMetodoPagoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatTipoCompensacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsBancoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoContratoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoRegimenContratacionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatAreaGeograficaRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatEstadoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.*;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.reportes.dto.EmpleadoReporte;
import mx.com.ga.cosmonaut.reportes.services.CargaEmpleadoService;
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
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class CargaEmpleadoServiceImpl implements CargaEmpleadoService {

    private static final Logger LOGG = LoggerFactory.getLogger(CargaEmpleadoServiceImpl.class);

    private RespuestaGenerica respuesta = new RespuestaGenerica();

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

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    @Inject
    private NclPoliticaRepository nclPoliticaRepository;

    @Inject
    private NclJornadaRepository nclJornadaRepository;

    @Inject
    private NclGrupoNominaRepository nclGrupoNominaRepository;

    @Inject
    private NclAreaRepository nclAreaRepository;

    @Inject
    private NclPuestoRepository nclPuestoRepository;

    @Override
    public RespuestaGenerica crearReporteEmpleado(Integer idEmpresa) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_EMPLEADO);){
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{"DatosEmpleados"};
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
                    RUTA_ARCHIVO_EMPLEADO) && agregarListasEmpleado(idEmpresa)) {
                    respuesta.setDatos(convertiraBase64(RUTA_ARCHIVO_EMPLEADO));
                    respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
                }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearReporteEmpleado " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean agregarListasEmpleado (Integer idEmpresa) {
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_EMPLEADO))){
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet realSheet = workbook.getSheetAt(0);

            crearListasEnCamposEmpleado(realSheet, workbook,idEmpresa);

            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA + RUTA_ARCHIVO_EMPLEADO);
            workbook.write(stream);
            stream.close();
            return Boolean.TRUE;
        } catch (IOException | ServiceException e) {
            LOGG.error("Se genero un error en agregarListasEmpleado -> ",e);
            return Boolean.FALSE;
        }
    }

    private void crearListasEnCamposEmpleado(XSSFSheet realSheet, XSSFWorkbook workbook, Integer idEmpresa)
            throws ServiceException {
        try {

            /**politicas. */
            List<String> empresaPolitica = nclPoliticaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByPoliticaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getPoliticaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**jornada. */
            List<String> empresaJornada = nclJornadaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByJornadaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getJornadaId() + SEPARADOR +
                            c.getTipoJornadaId().getTipoJornadaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**grupo nomina. */
            List<String> empresaGrupoNomina = nclGrupoNominaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdAndPagoComplementarioOrderByGrupoNominaId(true, idEmpresa, false)
                    .stream()
                    .map(c -> c.getGrupoNominaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**area. */
            List<String> empresaArea = nclAreaRepository
                    .findByEsActivoAndCentrocClienteIdOrderByAreaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getAreaId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            /**puesto. */
            List<String> empresaPuesto = nclPuestoRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByPuestoId(true,idEmpresa)
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
            valorDefaulCelda(LISTA_CELDA_VALOR_EMPLEADO, row);
            generaFormulaLista(workbook, validationHelper, listaMaster,
                    LISTA_CELDA_VALOR_EMPLEADO,2);
            formatoCelda(LISTA_CAMPOS_NUMEROS_EMPLEADO, workbook);
            realSheet.createFreezePane(0,2);
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearListasEnCamposEmpleado " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }
    
    @Override
    public RespuestaGenerica crearReporteListaEmpleado(Integer idEmpresa) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_LISTAEMPLEADO)){
            HashMap<String, Object> parametros = new HashMap<>();
            JasperPrint reporte = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());
            String[] nombreHoja = new String[]{"Lista Empleados"};
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

            ByteArrayInputStream input = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            //crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_LISTAEMPLEADO);
            String base64 = escribirDatosListaEmpleado(idEmpresa,input);
            respuesta.setDatos(base64);
            respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            /*if (Boolean.TRUE.equals(crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_LISTAEMPLEADO))
                && Boolean.TRUE.equals(escribirDatosListaEmpleado(idEmpresa))) {

            }*/
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearReporteListaEmpleado " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica crearReporteEmpleadoPPP(Integer idEmpresa) throws ServiceException {
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_EMPLEADOPPP)){
            HashMap<String, Object> parametros = new HashMap<>();

            JasperPrint reporte = JasperFillManager.fillReport(JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());

            String[] nombreHoja = new String[]{"Datos Empleados PPP"};
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
                    RUTA_ARCHIVO_EMPLEADOPPP) && agregarListasEmpleadoPPP(idEmpresa)) {
                respuesta.setDatos(convertiraBase64(RUTA_ARCHIVO_EMPLEADOPPP));
                respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearReporteEmpleadoPPP " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Boolean agregarListasEmpleadoPPP (Integer idEmpresa) {
        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_EMPLEADOPPP))){
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet realSheet = workbook.getSheetAt(0);

            crearListasEnCamposEmpleadoPPP(realSheet, workbook,idEmpresa);

            FileOutputStream stream = new FileOutputStream(RUTA_CARPETA + RUTA_ARCHIVO_EMPLEADOPPP);
            workbook.write(stream);
            stream.close();
            return Boolean.TRUE;
        } catch (IOException | ServiceException e) {
            LOGG.error("Se genero un error en agregarListasEmpleadoPPP -> ",e);
            return Boolean.FALSE;
        }
    }

    private void crearListasEnCamposEmpleadoPPP(XSSFSheet realSheet, XSSFWorkbook workbook, Integer idEmpresa)
            throws ServiceException {
        try {
            /**politicas. */
            List<String> empresaPolitica = nclPoliticaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByPoliticaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getPoliticaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**jornada. */
            List<String> empresaJornada = nclJornadaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByJornadaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getJornadaId() + SEPARADOR +
                            c.getTipoJornadaId().getTipoJornadaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**grupo nomina. */
            List<String> empresaGrupoNomina = nclGrupoNominaRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdAndPagoComplementarioOrderByGrupoNominaId(true, idEmpresa, true)
                    .stream()
                    .map(c -> c.getGrupoNominaId() + SEPARADOR + c.getNombre().trim())
                    .collect(Collectors.toList());

            /**area. */
            List<String> empresaArea = nclAreaRepository
                    .findByEsActivoAndCentrocClienteIdOrderByAreaId(true,idEmpresa)
                    .stream()
                    .map(c -> c.getAreaId() + SEPARADOR + c.getDescripcion().trim())
                    .collect(Collectors.toList());

            /**puesto. */
            List<String> empresaPuesto = nclPuestoRepository
                    .findByEsActivoAndCentrocClienteIdCentrocClienteIdOrderByPuestoId(true,idEmpresa)
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
            valorDefaulCelda(LISTA_CELDA_VALOR_EMPLEADOPPP, row);
            generaFormulaLista(workbook, validationHelper, listaMaster,
                    LISTA_CELDA_VALOR_EMPLEADOPPP,2);
            formatoCelda(LISTA_CAMPOS_NUMEROS_EMPLEADOPPP, workbook);
            realSheet.createFreezePane(0,2);
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " crearListasEnCamposEmpleadoPPP " +
                    mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }


    private String escribirDatosListaEmpleado(Integer idEmpresa,InputStream stream) throws ServiceException, IOException {

            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
            /** consulta. de empleados*/
            List<EmpleadosReporte> consultaEmpleadosReporte = empleadosReporteRepository.consultaEmpleados(idEmpresa);
            consultaEmpleadosReporte = consultaEmpleadosReporte.stream() .distinct().collect(Collectors.toList());
            String lista = gson.toJson(consultaEmpleadosReporte);
            Type listaEmpleadoReporte =
                    new TypeToken<List<EmpleadoReporte>>(){}.getType();

            List<EmpleadoReporte> empleadosReporte = gson.fromJson(lista, listaEmpleadoReporte);


                 InputStream file = stream;
                 XSSFWorkbook workbook = new XSSFWorkbook(file);

                XSSFSheet hojaActual = workbook.getSheetAt(0);
                hojaActual.createFreezePane(0,1);
                AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(1));
                AtomicInteger contEmpleado = new AtomicInteger(0);

                empleadosReporte
                        .stream()
                        .forEach(c -> {
                            headerfila.set(hojaActual.createRow(contEmpleado.get() + 1));
                            for (int j=0; j <= 47; j++) {
                                XSSFCell cell = headerfila.get().createCell(j);
                                cell.setCellValue(empleadosReporte.get(contEmpleado.get())
                                        .getProperty(String.valueOf(j)));
                    }
                    contEmpleado.getAndIncrement();
                });
                workbook.write(salida);
              return Base64.getEncoder().encodeToString(salida.toByteArray());
            }

    }



