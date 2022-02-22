package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrNominaXperiodoRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.CentroCostoCienteRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.orquestador.entity.*;
import mx.com.ga.cosmonaut.orquestador.repository.ReportesRepository;
import mx.com.ga.cosmonaut.orquestador.service.NominaOrdinariaLibService;
import mx.com.ga.cosmonaut.orquestador.service.NominasHistoricasLibService;
import mx.com.ga.cosmonaut.orquestador.service.ReporteNominaService;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.descargarecibos.Deduccion;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.reportenomina.ReporteNominaImpl;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;
import mx.com.ga.cosmonaut.reportes.services.ConexionClienteService;
import mx.com.ga.cosmonaut.reportes.services.NominaService;
import mx.com.ga.cosmonaut.reportes.util.UtileriasReporte;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.validaContenidoLista;

@Singleton
public class NominaServiceImpl  implements NominaService {

    private  RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Inject
    private ConexionClienteService conexionClienteService;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private NominaOrdinariaLibService nominaOrdinariaLibService;

    @Inject
    private ReportesRepository reportesRepository;

    @Inject
    private NcrNominaXperiodoRepository ncrNominaXperiodoRepository;

    @Inject
    private ReporteNominaService reporteNominaService;
    @Inject
    private NominasHistoricasLibService nominasHistoricasLibLibService;
    @Inject
    private CentroCostoCienteRepository clientePrd;

    @Override
    public RespuestaGenerica crearArchivoRFC(Integer nominaXperiodoId) throws ServiceException {
        String nombreArchivo = "/RFCNomina"+nominaXperiodoId;
        try  {
            respuestaGenerica.setDatos(escribirArchivo(nombreArchivo, nominaXperiodoId));
            respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            Files.delete(Paths.get(RUTA_CARPETA + nombreArchivo + ".txt"));
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" crearArchivoRFC " + Constantes.ERROR_EXCEPCION, ex);
        }
     return respuestaGenerica;
    }

    @Override
    public RespuestaGenerica crearLayoutDispersionNomina(ComprobanteFiscalModel comprobanteFiscalModel) throws ServiceException {
        //construccion del layout
        try (InputStream reporteJasper = this.getClass().getResourceAsStream(RUTA_JRXML_DISPERSIONNOMINA)){
            HashMap<String, Object> parametros = new HashMap<>();
            JasperPrint reporte = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(reporteJasper),
                    parametros, new JREmptyDataSource());
            String[] nombreHoja = new String[]{"Dispersión de Banco Nomina"};
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

            if (Boolean.TRUE.equals(
                    crearArchivo(reporte, configuration, byteArrayOutputStream, RUTA_ARCHIVO_DISPERSIONNOMINA))
            && Boolean.TRUE.equals(
                    escribirArchivoLayoutDispersionNomina(comprobanteFiscalModel.getNominaPeriodoId()))) {
                respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_DISPERSIONNOMINA));
                respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            }
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearLayoutDispersionNomina " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica crearLayoutDinamicoNominaExtraordinaria(Integer idEmpresa) throws ServiceException {
        List<String> listaCamposTitulo = Arrays.asList("Número de empleado", "Primer apellido", "Segundo apellido",
                "Nombre", "Domicilio del empleado", "Sede", "Entidad Federativa", "RFC", "CURP", "Grupo de nómina",
                "Puesto","Área", "Fecha de ingreso", "Fecha de antigüedad", "Método pago","Banco","Cuenta","CLABE",
                "Sueldo mensual","Salario base de cotización", "Gratificación anual (Aguinaldo)","Subsidio",
                "Total percepciones", "ISR","Total deducciones","Total neto", "Total neto en efectivo",
                "Total neto en especie", "Total IMSS", "Provisión vacaciones","Prestaciones en especie",
                "Prest. Esp. (Mayor 3 UMAS)", "Prestaciones en dinero", "Invalidéz y vida", "Riesgos de trabajo",
                "Guarderias", "Retiro", "Cesantia, edad avanzada y vejéz", "Gastos médicos para pens.","INFONAVIT",
                "Provisión prima vacacional", "Provisión gratificación anual", "ISN Ciudad de México","Total provisiones"
                );
        try(FileOutputStream outFile = new FileOutputStream(
                new File(RUTA_CARPETA + RUTA_ARCHIVO_NOMINAEXTRAORDINARIA));
            XSSFWorkbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Reporte de nómina");
            XSSFSheet hojaActual = workbook.getSheetAt(0);

            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(0));
            /**Nombre Empresa */
            headerfila.get().createCell(0).setCellValue("Nombre de la empresa");

            /** Nombre de la nómina*/
            headerfila.set(hojaActual.createRow(1));
            headerfila.get().createCell(0).setCellValue("Nombre de la nómina");

            /** Periodo: */
            headerfila.set(hojaActual.createRow(2));
            headerfila.get().createCell(0).setCellValue("Periodo:");


            headerfila.set(hojaActual.createRow(4));
            AtomicInteger i = new AtomicInteger(0);
            if (Boolean.TRUE.equals(validaContenidoLista(listaCamposTitulo))) {
                listaCamposTitulo
                        .stream()
                        .forEach(a -> {
                            XSSFCell cell = headerfila.get().createCell(i.get());
                            cell.setCellValue(a);
                            cell.setCellStyle(crearEstiloCeldas(workbook));
                            hojaActual.setColumnWidth(i.get(), 8000);
                            i.getAndIncrement();
                        });
            }
            workbook.write(outFile);
            respuestaGenerica.setDatos(convertiraBase64(RUTA_ARCHIVO_NOMINAEXTRAORDINARIA));
            respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);

            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " crearLayoutDinamicoNominaExtraordinaria " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica reporteNomina(ComprobanteFiscalModel comprobanteFiscalModel, Boolean esExtraordinaria) throws ServiceException {

        try {

            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<NominaPeriodoHistoricas> nominas = this.nominasHistoricasLibLibService.listaEmpleadosxNomina(comprobanteFiscalModel.getNominaPeriodoId());

            if(!nominas.isEmpty()){
               ByteArrayOutputStream salida =   creacionLayoutDinamico(nominas.get(0));
               String base64 = Base64.getEncoder().encodeToString(salida.toByteArray());
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setDatos(base64);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " reporteNomina " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }


    private String escribirArchivo(String nombreArchivo, Integer nominaXperiodoId) throws ServiceException {

        File archivoTxt = new File( RUTA_CARPETA + nombreArchivo + ".txt");

        try (FileWriter escribir = new FileWriter(archivoTxt, true)) {
            List<String> listaIdRfc = ncoContratoColaboradorRepository.recuperaIdAndRfc(nominaXperiodoId);
            if (Boolean.TRUE.equals(validaContenidoLista(listaIdRfc))) {
                listaIdRfc.stream().forEach(c -> {
                    try {
                        escribir.write(c + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " escribirArchivo " + Constantes.ERROR_EXCEPCION, ex);
        }
        return convertiraBase64(nombreArchivo + ".txt");
    }

    private Boolean escribirArchivoLayoutDispersionNomina(Integer nominaXperiodoId) throws ServiceException {
        try {
            Optional<NcrNominaXperiodo> nominaXperiodo =
                    ncrNominaXperiodoRepository.findById(nominaXperiodoId);

            if (!nominaXperiodo.isPresent()){
                new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_OBTENER_NOMINA);
            }
            List<NominaDispersion> lista = reportesRepository.reporteNominaDispersion(nominaXperiodoId);
            escribirDatosDispersionNomina(lista);
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " escribirArchivoLayout " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }


    private void escribirDatosDispersionNomina(List<NominaDispersion> empleadoADispersar)
    throws ServiceException {

        try (FileInputStream file = new FileInputStream(new File(RUTA_CARPETA +
                RUTA_ARCHIVO_DISPERSIONNOMINA));
                XSSFWorkbook workbook = new XSSFWorkbook(file);
             FileOutputStream outFile = new FileOutputStream(
                     new File(RUTA_CARPETA + RUTA_ARCHIVO_DISPERSIONNOMINA))) {

            XSSFSheet hojaActual = workbook.getSheetAt(0);
            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(1));
            AtomicInteger contEmpleado = new AtomicInteger(0);

            if (Boolean.TRUE.equals(validaContenidoLista(empleadoADispersar))) {
                empleadoADispersar
                        .stream()
                        .forEach(a -> {
                            headerfila.set(hojaActual.createRow(contEmpleado.get() + 1));
                            headerfila.get().createCell(0).setCellValue(a.getIdEmpleado() == null ? "" : a.getIdEmpleado());
                            headerfila.get().createCell(1).setCellValue(a.getNombre() == null ? "" : a.getNombre());
                            headerfila.get().createCell(2).setCellValue(a.getApellidos() == null ? "" : a.getApellidos());
                            headerfila.get().createCell(3).setCellValue(a.getRfc() == null ? "" : a.getRfc());
                            headerfila.get().createCell(4).setCellValue(a.getCurp() == null ? "" : a.getCurp());
                            headerfila.get().createCell(5).setCellValue(a.getMoneda() == null ? "" : a.getMoneda());
                            headerfila.get().createCell(6).setCellValue(a.getMontoAPagar() == null ? "" : a.getMontoAPagar());
                            headerfila.get().createCell(7).setCellValue(a.getBanco() == null ? "" : a.getBanco());
                            headerfila.get().createCell(8).setCellValue(a.getNumeroCuenta() == null ? "" : a.getNumeroCuenta());
                            headerfila.get().createCell(9).setCellValue(a.getNomina() == null ? "" : a.getNomina());
                            contEmpleado.getAndIncrement();
                        });
            }
            workbook.write(outFile);
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " escribirDatosDispersionNomina " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private ByteArrayOutputStream creacionLayoutDinamico(NominaPeriodoHistoricas reporteNomina) throws ServiceException{

        try(ByteArrayOutputStream outFile = new ByteArrayOutputStream();
            XSSFWorkbook workbook = new XSSFWorkbook()) {

            workbook.createSheet("Reporte de nómina");
            XSSFSheet hojaActual = workbook.getSheetAt(0);

            unirCeldas(hojaActual,0,0,0,3);
            unirCeldas(hojaActual,1,1,0,3);
            unirCeldas(hojaActual,2,2,0,3);
            unirCeldas(hojaActual,3,3,0,3);



            AtomicReference<XSSFRow> headerfilaTitulos = new AtomicReference<>(hojaActual.createRow(0));
            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(0));
            /**Nombre Empresa */
            headerfila.get().createCell(0).setCellValue("Nombre de la empresa : "
                    + reporteNomina.getRazonSocial());

            /** Nombre de la nómina*/
            headerfila.set(hojaActual.createRow(1));
            headerfila.get().createCell(0).setCellValue("Nombre de la nómina: "
                    + reporteNomina.getNombreNomina());

            /** Periodo: */
            headerfila.set(hojaActual.createRow(2));
            headerfila.get().createCell(0).setCellValue("Periodo: "+reporteNomina.getClavePeriodo());

            headerfila.set(hojaActual.createRow(3));
            headerfila.get().createCell(0)
                    .setCellValue("De " + new SimpleDateFormat("yyyy-MMM-dd").format(reporteNomina.getDel().getTime()) +
                            " hasta " + new SimpleDateFormat("yyyy-MMM-dd").format(reporteNomina.getAl().getTime()));
            System.out.println("De " + new SimpleDateFormat("yyyy-MMM-dd").format(reporteNomina.getDel().getTime()) +
                    " hasta " + new SimpleDateFormat("yyyy-MMM-dd").format(reporteNomina.getAl().getTime()));
            headerfilaTitulos.set(hojaActual.createRow(5));
                List<String> conceptoPercepcion = new ArrayList<>();
                List<String> conceptoDeduccion = new ArrayList<>();
                List<String> conceptoProvision = new ArrayList<>();

            reporteNomina.getEmpleados().stream().forEach(empleado -> {
                if(!empleado.getListaPercepciones().isEmpty()){
                    empleado.getListaPercepciones().stream().forEach(percepcion ->{
                        conceptoPercepcion.add(percepcion.getConceptoSat());
                    });
                }
                if(!empleado.getListaDeducciones().isEmpty()){
                    empleado.getListaDeducciones().stream().forEach(deduccion ->{
                        conceptoDeduccion.add(deduccion.getConceptoSat());
                    });
                }

                if(!empleado.getListaProvisiones().isEmpty()){
                    empleado.getListaProvisiones().stream().forEach(patronal ->{
                        conceptoProvision.add(patronal.getDescripcion());
                    });
                }
            });


                List<String> finalConceptoPercepcion = conceptoPercepcion.stream().distinct().collect(Collectors.toList());
                List<String> finalConceptoDeduccion = conceptoDeduccion.stream().distinct().collect(Collectors.toList());
                List<String> finalConceptoProvision = conceptoProvision.stream().distinct().collect(Collectors.toList());



                List<String> titulos = this.creacionTitulos(finalConceptoPercepcion,finalConceptoDeduccion,finalConceptoProvision);
            for(int x = 0; x < titulos.size(); x++){
                XSSFCell cell = headerfilaTitulos.get().createCell(x);
                cell.setCellValue(titulos.get(x).replace("-POLITICA","").replace("-PERSONA",""));
                cell.setCellStyle(crearEstiloCeldas(workbook));
                hojaActual.setColumnWidth(x, 8000);
            }

            Map<String,String> referencia = new HashMap<String,String>();
            titulos.stream().forEach(c -> {
                referencia.put(c,"");
            });

            AtomicInteger contadorFila = new AtomicInteger(6);
            AtomicInteger contadorEmpledados = new AtomicInteger(0);
            reporteNomina.getEmpleados().stream().forEach(empleado -> {
                headerfila.set(hojaActual.createRow(contadorFila.getAndIncrement()));
                List<Object> valores = this.getValores(empleado,finalConceptoPercepcion,finalConceptoDeduccion,finalConceptoProvision);
                AtomicInteger contador = new AtomicInteger(0);
                for(Object item : valores){
                    headerfila.get().createCell(contador.getAndIncrement()).setCellValue(String.valueOf(item == null?"":item));
                }
                contadorEmpledados.getAndIncrement();
            });


            headerfila.set(hojaActual.createRow(contadorFila.getAndIncrement()));
            XSSFCell celda =  headerfila.get().createCell(0);
            celda.setCellValue("Total "+contadorEmpledados.get());
            celda.setCellStyle(UtileriasReporte.crearEstiloBordeEspecial(workbook));




        workbook.write(outFile);

        return outFile;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " creacionLayoutDinamico " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private List<String> creacionTitulos(List<String> percepciones,List<String> deducciones,List<String> patronal){
        List<String> titulos = new ArrayList<String>();
        titulos.add("Numero de empleado");
        titulos.add("Nombre");
        titulos.add("Primero apellido");
        titulos.add("Segundo apellido");
        titulos.add("Puesto");
        titulos.add("Dias laborados");
        titulos.add("Dias ausencia");
        titulos.add("Dias incapacidad");
        titulos.add("Fecha contrato");
        titulos.add("Fecha antigüedad");
        titulos.add("Fecha pago timbrado");
        titulos.add("Metodo pago");
        titulos.add("Banco");
        titulos.add("Clabe");
        titulos.add("Cuenta bancaria");
        titulos.addAll(patronal);
        titulos.add("IMSS Patronal");
        titulos.add("ISN");
        titulos.add("Provisión aguinaldo");
        titulos.add("Provisión vacaciones");
        titulos.add("Provisión prima vacacional");
        titulos.add("Salario diario");
        titulos.add("Sueldo bruto mensual");
        titulos.addAll(percepciones);
        titulos.add("Total Percepciones");
        titulos.addAll(deducciones);
        titulos.add("Total Deducciones");
        titulos.add("Total Neto");
        return titulos;
    }
    public List<Object> getValores(ReporteReciboXEmpleado empleado, List<String> percepciones, List<String> deducciones, List<String> patronales){

        List<Object> respuesta = new ArrayList<>();
        respuesta.add(empleado.getNumEmpleado());
        respuesta.add(empleado.getNombre());
        respuesta.add(empleado.getApellidoPat());
        respuesta.add(empleado.getApellidoMat());
        respuesta.add(empleado.getPuesto());
        respuesta.add(empleado.getDiasLaborados());
        respuesta.add(empleado.getDiasAusencia());
        respuesta.add(empleado.getDiasIncapacidad());
        respuesta.add(new SimpleDateFormat("yyyy-MMM-dd").format(empleado.getFechaContrato()));
        respuesta.add(new SimpleDateFormat("yyyy-MMM-dd").format(empleado.getFechaAntiguedad()));
        if(empleado.getFechaPagoTimbrado()==null)
            respuesta.add("");
        else
            respuesta.add(new SimpleDateFormat("yyyy-MMM-dd").format(empleado.getFechaPagoTimbrado()));
        respuesta.add(empleado.getMetodoPago());
        respuesta.add(empleado.getBanco());
        respuesta.add(empleado.getClabe());
        respuesta.add(empleado.getNumCuenta());
        for(String item : patronales){
            String valor = "-";
            for(ProvisionporEmpleado patronal : empleado.getListaProvisiones()){
                if(patronal.getDescripcion().equalsIgnoreCase(item)){
                    valor = String.valueOf(patronal.getMontoProvision());
                    break;
                }
            }
            respuesta.add(valor);
        }
        respuesta.add(empleado.getProvisionImssPatronal());
        respuesta.add(empleado.getProvisionIsn());
        respuesta.add(empleado.getProvisionAguinaldo());
        respuesta.add(empleado.getProvisionVacaciones());
        respuesta.add(empleado.getProvisionPrimaVacacional());
        respuesta.add(empleado.getSalarioDiario());
        respuesta.add(empleado.getSueldoBrutoMensual());
        for(String item : percepciones){
            String valor = "-";
            for(Percepciones percepcion : empleado.getListaPercepciones()){
                if(percepcion.getConceptoSat().equalsIgnoreCase(item)){
                    valor = String.valueOf(percepcion.getMontoTotal());
                    break;
                }
            }
            respuesta.add(valor);
        }

        respuesta.add(empleado.getTotalPercepciones());
        for(String item : deducciones){
            String valor = "-";
            for(Deducciones deduccion : empleado.getListaDeducciones()){
                if(deduccion.getConceptoSat().equalsIgnoreCase(item)){
                    valor = String.valueOf(deduccion.getMontoCuota());
                    break;
                }
            }
            respuesta.add(valor);
        }
        respuesta.add(empleado.getTotalDeducciones());
        respuesta.add(empleado.getTotalNeto());

        return respuesta;

    }

    private List<Integer> validacionDatos(List<ReporteNominaImpl> colaboradores,
                                          List<String> listaCamposTitulo) throws ServiceException{

        try {
            List<Integer> listaValida = IntStream.iterate(0, i -> 0)
                    .limit(listaCamposTitulo.size()).boxed().collect(Collectors.toList());

            if (Boolean.TRUE.equals(validaContenidoLista(colaboradores))) {
                colaboradores.stream().forEach(c -> {
                    if (c.getReporteNomina().getNombre() == null) {
                        listaValida.set(0, listaValida.get(0) + 1);
                    }
                    if (c.getReporteNomina().getApellidoPat() == null) {
                        listaValida.set(1, listaValida.get(1) + 1);
                    }
                    if (c.getReporteNomina().getApellidoMat() == null) {
                        listaValida.set(2, listaValida.get(2) + 1);
                    }
                    if (c.getReporteNomina().getRfc() == null) {
                        listaValida.set(3, listaValida.get(3) + 1);
                    }
                    if (c.getReporteNomina().getCurp() == null) {
                        listaValida.set(4, listaValida.get(4) + 1);
                    }
                    if (c.getReporteNomina().getNss() == null) {
                        listaValida.set(5, listaValida.get(5) + 1);
                    }
                    if (c.getReporteNomina().getNumEmpleado() == null) {
                        listaValida.set(6, listaValida.get(6) + 1);
                    }
                    if (c.getReporteNomina().getSbc() == null) {
                        listaValida.set(7, listaValida.get(7) + 1);
                    }
                    if (c.getReporteNomina().getDiasLaborados() == null) {
                        listaValida.set(8, listaValida.get(8) + 1);
                    }
                    if (c.getReporteNomina().getDiasAusencia() == null) {
                        listaValida.set(9, listaValida.get(9) + 1);
                    }
                    if (c.getReporteNomina().getDiasIncapacidad() == null) {
                        listaValida.set(10, listaValida.get(10) + 1);
                    }
                    if (c.getReporteNomina().getTotalPercepciones() == null) {
                        listaValida.set(11, listaValida.get(11) + 1);
                    }
                    if (c.getReporteNomina().getTotalDeducciones() == null) {
                        listaValida.set(12, listaValida.get(12) + 1);
                    }
                    if (c.getReporteNomina().getTotalNeto() == null) {
                        listaValida.set(13, listaValida.get(13) + 1);
                    }
                    if (c.getReporteNomina().getTotalProvisiones() == null) {
                        listaValida.set(14, listaValida.get(14) + 1);
                    }
                    if (c.getReporteNomina().getFechaContrato() == null) {
                        listaValida.set(15, listaValida.get(15) + 1);
                    }
                    if (c.getReporteNomina().getPuesto() == null) {
                        listaValida.set(16, listaValida.get(16) + 1);
                    }
                    if (c.getReporteNomina().getFechaPagoTimbrado() == null) {
                        listaValida.set(17, listaValida.get(17) + 1);
                    }
                    if (c.getReporteNomina().getMetodoPago() == null) {
                        listaValida.set(18, listaValida.get(18) + 1);
                    }
                    if (c.getReporteNomina().getMoneda() == null) {
                        listaValida.set(19, listaValida.get(19) + 1);
                    }
                    if (c.getReporteNomina().getBanco() == null) {
                        listaValida.set(20, listaValida.get(20) + 1);
                    }
                    if (c.getReporteNomina().getFechaAntiguedad() == null) {
                        listaValida.set(21, listaValida.get(21) + 1);
                    }
                    if (c.getReporteNomina().getFechaIngreso() == null) {
                        listaValida.set(22, listaValida.get(22) + 1);
                    }
                    if (c.getReporteNomina().getClabe() == null) {
                        listaValida.set(23, listaValida.get(23) + 1);
                    }
                    if (c.getReporteNomina().getNumCuenta() == null) {
                        listaValida.set(24, listaValida.get(24) + 1);
                    }
                    if (c.getReporteNomina().getCuentaBancareaia() == null) {
                        listaValida.set(25, listaValida.get(25) + 1);
                    }
                    if (c.getReporteNomina().getProvisionImssPatronal() == null) {
                        listaValida.set(26, listaValida.get(26) + 1);
                    }
                    if (c.getReporteNomina().getProvisionIsn() == null) {
                        listaValida.set(27, listaValida.get(27) + 1);
                    }
                    if (c.getReporteNomina().getProvisionVacaciones() == null) {
                        listaValida.set(28, listaValida.get(28) + 1);
                    }
                    if (c.getReporteNomina().getProvisionAguinaldo() == null) {
                        listaValida.set(29, listaValida.get(29) + 1);
                    }
                    if (c.getReporteNomina().getProvisionPrimaVacacional() == null) {
                        listaValida.set(30, listaValida.get(30) + 1);
                    }
                    if (c.getReporteNomina().getSalarioDiario() == null) {
                        listaValida.set(31, listaValida.get(31) + 1);
                    }
                    if (c.getReporteNomina().getSueldoBrutoMensual() == null) {
                        listaValida.set(32, listaValida.get(32) + 1);
                    }
                    if (c.getReporteNomina().getDescripcionAreaea() == null) {
                        listaValida.set(33, listaValida.get(33) + 1);
                    }
                    if (c.getReporteNomina().getDescripcionSede() == null) {
                        listaValida.set(34, listaValida.get(34) + 1);
                    }
                    if (c.getReporteNomina().getEntidadFederativaColaborador() == null) {
                        listaValida.set(35, listaValida.get(35) + 1);
                    }
                    if (c.getReporteNomina().getDireccion() == null) {
                        listaValida.set(36, listaValida.get(36) + 1);
                    }

                });
            }
            return IntStream
                    .range(0,listaValida.size())
                    .filter(c -> listaValida.get(c) != colaboradores.size())
                    .boxed()
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " validacionDatos " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }
}


