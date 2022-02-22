package mx.com.ga.cosmonaut.reportes.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.FolioFiscalConsulta;
import mx.com.ga.cosmonaut.common.dto.reportes.TablaBasicaDto;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.common.service.DocumentosEmpleadoService;
import mx.com.ga.cosmonaut.common.service.ReporteService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.Nomina;
import mx.com.ga.cosmonaut.orquestador.entity.*;
import mx.com.ga.cosmonaut.orquestador.repository.ReporteNominaRepository;
import mx.com.ga.cosmonaut.orquestador.service.EmpleadosAcumuladosServices;
import mx.com.ga.cosmonaut.orquestador.service.impl.NominasHistoricasLibLibServiceImpl;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladoconcepto.Percepciones;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladoconcepto.Provisiones;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladoconcepto.ReporteNominaHistorica;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladoconcepto.ReporteNominaHistoricaImpl;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.acumuladonomina.ReporteNominaImpl;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.detallenominahistorica.ReporteDetalleNominaImpl;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable.AgrupadorDeducciones;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable.AgrupadorPercepciones;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable.PolizaContable;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.polizacontable.PolizaContableImpl;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoConcepto;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoHistorica;
import mx.com.ga.cosmonaut.reportes.model.FolioFiscal;
import mx.com.ga.cosmonaut.reportes.services.ConexionClienteService;
import mx.com.ga.cosmonaut.reportes.services.NominaHistoricaService;
import mx.com.ga.cosmonaut.reportes.util.UtileriasReporte;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class NominaHistoricaServiceImpl implements NominaHistoricaService {

    @Inject
    private ConexionClienteService conexionClienteService;

    @Inject
    private DocumentosEmpleadoService documentosEmpleadoService;

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    @Inject
    private ReporteService reporteService;

    @Inject
    private NominasHistoricasLibLibServiceImpl nominasHistoricasLibLibService;

    @Inject
    private EmpleadosAcumuladosServices acumuladosPrd;

    @Inject
    private NclCentrocClienteRepository centroClientePrd;

    @Inject
    private ReporteNominaRepository reporteNominaPrd;

    private String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    @Override
    public RespuestaGenerica polizaContable(Integer nominaXperiodoId) throws ServiceException {
        try {

            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<ReportePolizaContable> listaPoliza = nominasHistoricasLibLibService.reportePolizaContable(nominaXperiodoId);
            if (!listaPoliza.isEmpty()) {
               ByteArrayOutputStream output =  crearArchivoExcelPolizaContable(listaPoliza.get(0));
               String base64 = Base64.getEncoder().encodeToString(output.toByteArray());
                respuesta.setDatos(base64);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" polizaContable " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica reporteRaya(Integer nominaXperiodoId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NominaPeriodoHistoricas> nominas = this.nominasHistoricasLibLibService.listaReporteRaya(nominaXperiodoId);

            if(!nominas.isEmpty()){
                Type collecReporteRaya = new
                        TypeToken<mx.com.ga.cosmonaut.reportes.dto.respuesta.reporteraya.ReporteNominaHistoricaImpl>(){}.getType();

                InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/RayaNominaHistorica.jrxml");

                HashMap<String, Object> parametros =
                        obtenerDatosRayaNomina(nominas.get(0));
                JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros);
                String base64 = Base64.getEncoder().encodeToString(JasperExportManager.exportReportToPdf(reporte));
                respuesta.setDatos(base64);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }


            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" reporteRaya " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica acumuladoConcepto(AcumuladoConcepto acumuladoConcepto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            CentroClienteAcumulado centroCliente = acumuladosPrd.getAcumuladosPorConcepto(acumuladoConcepto.getClienteId(),acumuladoConcepto.getMes(),acumuladoConcepto.getAnio());
            if (centroCliente != null) {
                Type collecAcumuladoConcepto = new
                        TypeToken<List<ReporteNominaHistoricaImpl>>(){}.getType();



                InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/AcumuladoXconcepto.jrxml");

                HashMap<String, Object> parametros =
                        obtenerDatosAcumuladoConcepto(centroCliente,acumuladoConcepto);

                parametros.put("pListaProvisiones",obtenInformacionProvisiones(centroCliente.getProvisiones()));
                parametros.put("pTituloProvisiones","PROVISIONES");
                parametros.put("pListaPercepciones",obtenInformacionPercepciones(centroCliente.getPercepciones()));
                parametros.put("pTituloPercepciones","PERCEPCIONES");
                parametros.put("pListaDeducciones",obtenInformacionPercepciones(centroCliente.getDeducciones()));
                parametros.put("pTituloDeducciones","DEDUCCIONES");

                JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros);

                String base64 = Base64.getEncoder().encodeToString(JasperExportManager.exportReportToPdf(reporte));

                respuesta.setDatos(base64);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
                
            }else{
                respuesta.setMensaje("No existe acumulados para el periodo seleccionado");
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" acumuladoConcepto " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica folioFiscal(FolioFiscal folioFiscal) throws ServiceException {
        try {

            /** listado de datos sobre folio fiscal*/
            List<FolioFiscalConsulta> folioFiscalConsultas = consultaFolioFiscal(folioFiscal);

            if (!folioFiscalConsultas.isEmpty()) {
                InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/folioFiscal.jrxml");
                HashMap<String, Object> parametros = new HashMap<>();

                parametros.put("pNombreNomina",folioFiscalConsultas.get(0).getNombreNomina());
                parametros.put("pClavePeriodo","Periódo: " + folioFiscalConsultas.get(0).getClavePeriodo());
                parametros.put("pFecha","De " + folioFiscalConsultas.get(0).getFechaInicio() +
                        " hasta " + folioFiscalConsultas.get(0).getFechaFin());

                JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(folioFiscalConsultas);
                JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros,datos);

                respuestaGenerica.setDatos(reporteService.generaReportePDF(reporte));
                respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
                respuestaGenerica.setMensaje(Constantes.EXITO);
            } else {
                respuestaGenerica.setDatos("");
                respuestaGenerica.setResultado(Constantes.RESULTADO_ERROR);
                respuestaGenerica.setMensaje("No se encontraron folios fiscales");
            }

            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" folioFiscal " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica detalleNominaHistorica(Integer nominaXperiodoId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NominaPeriodoHistoricas> nominas = this.nominasHistoricasLibLibService.listaReporteRaya(nominaXperiodoId);
            if(!nominas.isEmpty()){
                 //Obtener Provisiones
                NominaPeriodoHistoricas nominaSeleccionada = nominas.get(0);
                if(!nominaSeleccionada.getEmpleados().isEmpty()){
                     nominaSeleccionada.getEmpleados().stream().forEach(empleado ->{
                         try {
                             empleado.setListaProvisiones(reporteNominaPrd.obtenerProvisionPorEmpleado(nominaXperiodoId,empleado.getPersonaId()));
                         } catch (ServiceException e) {
                             e.printStackTrace();
                         }
                     });
                }
                InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/DetalleNominaHistoricas.jrxml");
                HashMap<String, Object> parametros = new HashMap<>();
                parametros.put("pRazonSocial",nominaSeleccionada.getRazonSocial());
                parametros.put("pNombreNomina",nominaSeleccionada.getNombreNomina());
                parametros.put("pClavePeriodo",nominaSeleccionada.getClavePeriodo());
                parametros.put("pFechaPeriodo",new SimpleDateFormat("dd-MM-yyyy").format(nominaSeleccionada.getDel().getTime()) +
                        " al " + new SimpleDateFormat("dd-MM-yyyy").format(nominaSeleccionada.getAl().getTime()));

                /***/
                parametros.put("pListaCalculoEmpleado",nominaSeleccionada.getEmpleados());

                JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros);
                String base64 = Base64.getEncoder().encodeToString(JasperExportManager.exportReportToPdf(reporte));

                respuesta.setDatos(base64);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
            }
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" detalleNominaHistorica " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica acumuladoNominaHistorica(AcumuladoHistorica acumuladoHistorica) throws ServiceException {
        try {

            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<EmpleadoXNominaAcumulados> lista = this.acumuladosPrd.getAcumuladosPorMes(acumuladoHistorica.getClienteId(),acumuladoHistorica.getMes(),acumuladoHistorica.getAnio());

            if(!lista.isEmpty()){
                ByteArrayOutputStream  mm = creacionDinamicaAcumuladoNominaHistorica(lista,acumuladoHistorica);
                    respuesta.setDatos(Base64.getEncoder().encodeToString(mm.toByteArray()));
                respuesta.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);

            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje("No hay acumulados por nómina para el mes seleccionado");
            }




            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" acumuladoNominaHistorica " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private ByteArrayOutputStream creacionDinamicaAcumuladoNominaHistorica(List<EmpleadoXNominaAcumulados> listaEmpleados,AcumuladoHistorica acumuladoHistorica) throws ServiceException {

        try(ByteArrayOutputStream outFile = new ByteArrayOutputStream();
            XSSFWorkbook workbook = new XSSFWorkbook()) {

            Optional<NclCentrocCliente> cliente = this.centroClientePrd.findById(acumuladoHistorica.getClienteId());
            String nombreCliente = "";
            if(cliente.isPresent()){
                nombreCliente =  cliente.get().getRazonSocial();
            }




            workbook.createSheet(String.format("%1$s %2$s",meses[acumuladoHistorica.getMes()-1],"2021"));
            XSSFSheet hojaActual = workbook.getSheetAt(0);

            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(0));
            /**Nombre Empresa */
            headerfila.get().createCell(0).setCellValue("Nombre de la empresa: " + nombreCliente);
            unirCeldas(hojaActual,0,0,0,3);
            /** Mes */
            headerfila.set(hojaActual.createRow(1));
            headerfila.get().createCell(0).setCellValue("Mes: " + meses[acumuladoHistorica.getMes()-1]);
            unirCeldas(hojaActual,1,1,0,3);
            headerfila.set(hojaActual.createRow(4));

             //----------Empieza el detalle---------------------


                    AtomicInteger numColaborador = new AtomicInteger(0);
                    List<String> conceptoPercepcion = new ArrayList<>();
                    List<String> conceptoDeduccion = new ArrayList<>();
                    List<String> conceptoPatronal = new ArrayList<>();

                    listaEmpleados.stream().forEach(empleado -> {
                        if(!empleado.getPercepciones().isEmpty()){
                            empleado.getPercepciones().stream().forEach(percepcion ->{
                                conceptoPercepcion.add(percepcion.getConceptoSat());
                            });
                        }
                        if(!empleado.getDeducciones().isEmpty()){
                            empleado.getDeducciones().stream().forEach(deduccion ->{
                                conceptoDeduccion.add(deduccion.getConceptoSat());
                            });
                        }

                        if(!empleado.getPatronal().isEmpty()){
                            empleado.getPatronal().stream().forEach(patronal ->{
                                conceptoPatronal.add(patronal.getConceptoSat());
                            });
                        }
                    });

                    List<String> finalConceptoPercepcion = conceptoPercepcion.stream().distinct().collect(Collectors.toList());
                    List<String> finalConceptoDeduccion = conceptoDeduccion.stream().distinct().collect(Collectors.toList());
                    List<String> finalConceptoPatronal = conceptoPatronal.stream().distinct().collect(Collectors.toList());
                    AtomicReference<XSSFRow> headerfilaTitulos = new AtomicReference<>(hojaActual.createRow(3));

             List<String> titulos = this.creacionTitulos(finalConceptoPercepcion,finalConceptoDeduccion,finalConceptoPatronal);

            headerfila.set(hojaActual.createRow(2));
            for(int x = 0; x < titulos.size(); x++){
                XSSFCell cell = headerfilaTitulos.get().createCell(x);
                cell.setCellValue(titulos.get(x));
                cell.setCellStyle(crearEstiloCeldas(workbook));
                hojaActual.setColumnWidth(x, 8000);
            }

            AtomicInteger contadorFila = new AtomicInteger(4);
            Map<String,String> referencia = new HashMap<String,String>();
            titulos.stream().forEach(c -> {
                referencia.put(c,"");
            });
            listaEmpleados.stream().forEach(empleado -> {
                headerfila.set(hojaActual.createRow(contadorFila.getAndIncrement()));
                List<Object> valores = this.getValores(empleado,finalConceptoPercepcion,finalConceptoDeduccion,finalConceptoPatronal,referencia);
                AtomicInteger contador = new AtomicInteger(0);
                for(Object item : valores){
                    headerfila.get().createCell(contador.getAndIncrement()).setCellValue(String.valueOf(item == null?"":item));
                }
            });

            headerfila.set(hojaActual.createRow(contadorFila.getAndIncrement()));
            AtomicInteger contador = new AtomicInteger(0);

            List<String> valoresTotales = new ArrayList<String>();
            titulos.stream().forEach(c ->{
                valoresTotales.add(referencia.get(c));
            });

            valoresTotales.stream().forEach(c -> {
                try {
                    UtileriasReporte.crearCeldaBorde(contador.getAndIncrement(),workbook,headerfila,c);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            });

            //------------------------------------

            workbook.write(outFile);

            return outFile;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" creacionDinamicaAcumuladoNominaHistorica " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private List<String> creacionTitulos(List<String> percepciones,List<String> deducciones,List<String> patronal){
        List<String> titulos = new ArrayList<String>();
        titulos.add("Numero de empleado");
        titulos.add("Nombre");
        titulos.add("Primero apellido");
        titulos.add("Segundo apellido");
        titulos.add("Puesto");
        titulos.add("Fecha ingreso");
        titulos.add("Fecha de antiguedad");
        titulos.add("Fecha baja");
        titulos.add("RFC");
        titulos.add("CURP");
        titulos.add("NSS");
        titulos.add("Sueldo bruto");
        titulos.add("SBC");
        titulos.add("Provisión prima vacacional");
        titulos.add("Provisión vacaciones");
        titulos.add("Provisión aguinaldo");
        titulos.add("Provisión ISN");
        titulos.addAll(patronal);
        titulos.add("Total IMSS Patronal");
        titulos.addAll(percepciones);
        titulos.add("Total percepciones");
        titulos.addAll(deducciones);
        titulos.add("Total deducciones");
        titulos.add("Total neto");
        return titulos;
    }

    public List<Object> getValores(EmpleadoXNominaAcumulados empleado,List<String> percepciones,List<String> deducciones,List<String> patronales, Map<String,String> referencia){

        List<Object> respuesta = new ArrayList<>();
        respuesta.add(empleado.getNumEmpleado());
        respuesta.add(empleado.getNombre());
        respuesta.add(empleado.getApellidoPat());
        respuesta.add(empleado.getApellidoMat());
        respuesta.add(empleado.getPuesto());
        respuesta.add(empleado.getFechaAntiguedad());
        respuesta.add(empleado.getFechaIngreso());
        respuesta.add(empleado.getFechaBaja());
        respuesta.add(empleado.getRfc());
        respuesta.add(empleado.getCurp());
        respuesta.add(empleado.getNss());
        respuesta.add(empleado.getSueldoBrutoMensual());
        referencia.replace("Sueldo bruto",valor(referencia.get("Sueldo bruto"),empleado.getSueldoBrutoMensual()));
        respuesta.add(empleado.getSbc());
        referencia.replace("SBC",valor(referencia.get("SBC"),empleado.getSbc()));
        respuesta.add(empleado.getProvisionPrimaVacacional());
        referencia.replace("Provisión prima vacacional",valor(referencia.get("Provisión prima vacacional"),empleado.getProvisionPrimaVacacional()));
        respuesta.add(empleado.getProvisionVacaciones());
        referencia.replace("Provisión vacaciones",valor(referencia.get("Provisión vacaciones"),empleado.getProvisionVacaciones()));
        respuesta.add(empleado.getProvisionAguinaldo());
        referencia.replace("Provisión aguinaldo",valor(referencia.get("Provisión aguinaldo"),empleado.getProvisionAguinaldo()));
        respuesta.add(empleado.getProvisionIsn());
        referencia.replace("Provisión ISN",valor(referencia.get("Provisión ISN"),empleado.getProvisionIsn()));
        for(String item : patronales){
            String valor = "-";
            for(PercepcionesDeduccionesAcumulados patronal : empleado.getPatronal()){
                if(patronal.getConceptoSat().equalsIgnoreCase(item)){
                    valor = String.valueOf(patronal.getImporte());
                    referencia.replace(item,valor(referencia.get(item),patronal.getImporte()));
                    break;
                }
            }
            respuesta.add(valor);
        }
        respuesta.add(empleado.getProvisionImssPatronal());
        referencia.replace("Total IMSS Patronal",valor(referencia.get("Total IMSS Patronal"),empleado.getProvisionImssPatronal()));

       for(String item : percepciones){
            String valor = "-";
            for(PercepcionesDeduccionesAcumulados percepcion : empleado.getPercepciones()){
                if(percepcion.getConceptoSat().equalsIgnoreCase(item)){
                    valor = String.valueOf(percepcion.getImporte());
                    referencia.replace(item,valor(referencia.get(item),percepcion.getImporte()));
                    break;
                }
            }
            respuesta.add(valor);
        }
        respuesta.add(empleado.getTotalPercepciones());
        referencia.replace("Total percepciones",valor(referencia.get("Total percepciones"),empleado.getTotalPercepciones()));
         for(String item : deducciones){
            String valor = "-";
            for(PercepcionesDeduccionesAcumulados deduccion : empleado.getDeducciones()){
                if(deduccion.getConceptoSat().equalsIgnoreCase(item)){
                    valor = String.valueOf(deduccion.getImporte());
                    referencia.replace(item,valor(referencia.get(item),deduccion.getImporte()));
                    break;
                }
            }
            respuesta.add(valor);
        }

        respuesta.add(empleado.getTotalDeducciones());
        referencia.replace("Total deducciones",valor(referencia.get("Total deducciones"),empleado.getTotalDeducciones()));
        respuesta.add(empleado.getTotalNeto());
        referencia.replace("Total neto",valor(referencia.get("Total neto"),empleado.getTotalNeto()));
        return respuesta;

    }

    private HashMap<String, Object> obtenerDatosAcumuladoConcepto(CentroClienteAcumulado rnh,AcumuladoConcepto acumuladoConcepto) throws ServiceException {
        try {

            Double sumaPercepciones = rnh.getPercepciones().stream().mapToDouble(percepcion -> percepcion.getImporte()).sum();
            Double sumaDeducciones = rnh.getDeducciones().stream().mapToDouble(deduccion -> deduccion.getImporte()).sum();

            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("pNombreEmpresa",rnh.getRazonSocial());
            parametros.put("pPeriodo","Periodo :  "+String.format("%1$s %2$s",meses[acumuladoConcepto.getMes()-1],acumuladoConcepto.getAnio()));
            parametros.put("pRFC",rnh.getRfc());
            parametros.put("pRegistroPatronal",rnh.getRegistroPatronalImss());
            parametros.put("pNetoApagarEfectivo","$" + rnh.getNetoApagarEfectivo());
            parametros.put("pNetoApagarEspecie","$" + rnh.getNetoApagarEspecie());
            parametros.put("pNetoApagar","$" + UtileriasReporte.redondeoCantidad(String.valueOf((sumaPercepciones-sumaDeducciones)),2));
            return parametros;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerDatosAcumuladoConcepto " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private HashMap<String, Object> obtenerDatosRayaNomina(NominaPeriodoHistoricas reporteRaya) throws ServiceException {
        try {
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("pFechaListaRayaAl",(new SimpleDateFormat("yyyy-MM-dd").format(reporteRaya.getAl().getTime())));
            parametros.put("pFechaListaRayaDel",(new SimpleDateFormat("yyyy-MM-dd").format(reporteRaya.getDel().getTime())));
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            format1.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm");
            format2.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
            parametros.put("pFechaDia",format1.format(reporteRaya.getFechaHora().getTime()));
            parametros.put("pFechaHora",format2.format(reporteRaya.getFechaHora().getTime()));
            parametros.put("pListaDetalleXempleado",reporteRaya.getEmpleados());
            parametros.put("pRazonSocial",reporteRaya.getRazonSocial());
            parametros.put("pDomicilio",reporteRaya.getDomicilio());
            parametros.put("pRfc",reporteRaya.getRfc());
            parametros.put("pRegistroPatronal",reporteRaya.getRegistroPatronalImss());
            parametros.put("pNombreNomina",reporteRaya.getNombreNomina());

            return parametros;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerDatosRayaNomina " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private List<FolioFiscalConsulta> consultaFolioFiscal(FolioFiscal folioFiscal) throws ServiceException {
        try {
            List<FolioFiscalConsulta> folioFiscalConsultas = empleadosReporteRepository
                    .foliofiscales(folioFiscal.getNominaXperiodoId());

                if (!folioFiscal.getListaIdPersona().isEmpty()) {
                    folioFiscalConsultas = folioFiscalConsultas
                            .stream()
                            .filter(c -> folioFiscal.getListaIdPersona().contains(c.getPersonaId()))
                            .collect(Collectors.toList());
                }
            return folioFiscalConsultas;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" consultaFolioFiscal " + Constantes.ERROR_EXCEPCION, ex);
        }
    }



    private RespuestaGenerica consultaServicio(Integer nominaXperiodoId, Integer numServicio) throws ServiceException {
        try {
            return conexionClienteService.conectarClienteServicio
                    (nominaXperiodoId,numServicio);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" consultaServicio " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private ByteArrayOutputStream crearArchivoExcelPolizaContable(ReportePolizaContable polizaContable) throws ServiceException{
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outFile = new ByteArrayOutputStream()){

            workbook.createSheet("Póliza contable");
            XSSFSheet hojaActual = workbook.getSheetAt(0);

            CellStyle estilos = workbook.createCellStyle();
            XSSFFont fuente = workbook.createFont();
            fuente.setBold(true);
            estilos.setFont(fuente);

            /**creacion de fila*/
            AtomicReference<XSSFRow> fila = new AtomicReference<>(hojaActual.createRow(0));

            unirCeldas(hojaActual,0,0,0,3);
            /**Nombre Empresa */
            XSSFCell celda =  fila.get().createCell(0);
            celda.setCellValue("Nombre de la empresa : " +
                    polizaContable.getNombreCliente());
            celda.setCellStyle(estilos);

            unirCeldas(hojaActual,1,1,0,3);
            /** nombre nomina*/
            fila.set(hojaActual.createRow(1));
            celda = fila.get().createCell(0);
            celda.setCellValue("Nombre de la nómina : " +
                    polizaContable.getNombreNomina());
            celda.setCellStyle(estilos);

            unirCeldas(hojaActual,2,2,0,3);
            /** nombre nomina*/
            fila.set(hojaActual.createRow(2));
            celda = fila.get().createCell(0);
            celda.setCellValue("Periodo : De " +
                    polizaContable.getFechaInicio() + " hasta " +
                    polizaContable.getFechaFin());
            celda.setCellStyle(estilos);


            /** encabezados*/
            fila.set(hojaActual.createRow(4));
            crearEncabezadosEstilo(fila,workbook,0,"Cuenta contable",hojaActual);
            crearEncabezadosEstilo(fila,workbook,1,"Conceptos",hojaActual);
            crearEncabezadosEstilo(fila,workbook,2,"Cargo",hojaActual);
            crearEncabezadosEstilo(fila,workbook,3,"Abono",hojaActual);

            /**contador universal*/
            AtomicInteger numFila = new AtomicInteger(5);

            AtomicReference<Double> sumaPercepciones = new AtomicReference<>(0.0);
            AtomicReference<Double> sumaDeducciones = new AtomicReference<>(0.0);

            /** contenido de tabla*/
            if (!polizaContable.getAgrupadorPercepciones().isEmpty()) {
                contenidoAgrupadorPercepciones(numFila, polizaContable,
                        fila,workbook, hojaActual);
                sumaPercepciones = (sumaPercepciones(polizaContable.getAgrupadorPercepciones()));
            }
            if (!polizaContable.getAgrupadorDeducciones().isEmpty()) {
                contenidoAgrupadorDeducciones(numFila, polizaContable,
                        fila,workbook, hojaActual);
                sumaDeducciones = sumaDeducciones(polizaContable.getAgrupadorDeducciones());
            }

            Double sumaProvisiones = polizaContable.getProvisionAguinaldo()+polizaContable.getProvisionImss()+polizaContable.getProvisionIsn()+polizaContable.getProvisionVacaciones()+polizaContable.getProvisionPrimaVacacional();
            Double sumaNeto = sumaPercepciones.get()-sumaDeducciones.get();

            numFila.getAndIncrement();
            fila.set(hojaActual.createRow(numFila.getAndIncrement()));

            CellStyle estilo = workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.RIGHT);
            fila.get().createCell(0)
                    .setCellValue(validaContenido("NET01"));
            fila.get().createCell(1).setCellValue("Neto");
            celda  = fila.get().createCell(3);
            celda.setCellValue("$" + UtileriasReporte.redondeoCantidad(String.valueOf(sumaNeto),2));
            celda.setCellStyle(estilo);
            fila.get().createCell(2)
                    .setCellValue("" );



            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"SUB TOTAL");
            crearCeldaBordeEspecial(2,workbook,fila,"");
            crearCeldaBordeEspecial(3,workbook,fila,"$" + Precision.round(sumaNeto,2));




            numFila.getAndIncrement();
            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"GRAN TOTAL");
            crearCeldaBordeEspecial(2,workbook,fila,"$" + Precision.round((sumaPercepciones.get()+sumaProvisiones),2));
            crearCeldaBordeEspecial(3,workbook,fila,"$" + Precision.round((sumaDeducciones.get()+sumaProvisiones+sumaNeto),2));

            workbook.write(outFile);
            return outFile;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" crearArchivoExcelPolizaContable " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private void crearEncabezadosEstilo(AtomicReference<XSSFRow> fila, XSSFWorkbook workbook,
                                        Integer celda,String valor,XSSFSheet hojaActual) throws ServiceException {
        try {
            Cell cell = fila.get().createCell(celda);
            cell.setCellValue(valor);
            cell.setCellStyle(crearEstiloCeldas(workbook));
            hojaActual.setColumnWidth(celda,8000);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" crearEncabezadosEstilo " + Constantes.ERROR_EXCEPCION, ex);
        }
    }



    private void contenidoAgrupadorPercepciones(AtomicInteger numFila,ReportePolizaContable ap,
                                                AtomicReference<XSSFRow> fila, XSSFWorkbook workbook,
                                                XSSFSheet hojaActual) throws ServiceException {
        try {
            numFila.getAndIncrement();
            AtomicReference<Double> suma = new AtomicReference<>(0.0);
            XSSFCellStyle estilo = workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.RIGHT);
            XSSFCell celda;
            for(SumaPercepcion c :ap.getAgrupadorPercepciones()){

                fila.set(hojaActual.createRow(numFila.get()));
                try {
                    fila.get().createCell(0)
                            .setCellValue(validaContenido(c.getCuentaContable()));
                    fila.get().createCell(1).setCellValue(validaContenido(c.getConceptoSat()));
                    celda = fila.get().createCell(2);
                    celda.setCellValue("$" + validaContenido(c.getImporte()));
                    celda.setCellStyle(estilo);
                    fila.get().createCell(3)
                            .setCellValue("" );

                    suma.updateAndGet(v -> v + Double.parseDouble(c.getImporte()));
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                numFila.getAndIncrement();
            }
            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"SUB TOTAL");
            crearCeldaBordeEspecial(2,workbook,fila,"$" + Precision.round(suma.get(),2));
            crearCeldaBordeEspecial(3,workbook,fila,"");

            //Provisiones
            numFila.getAndIncrement();
            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            celda =  fila.get().createCell(0);
            celda.setCellValue(validaContenido("PROV01"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Impuesto sobre nómina"));
            celda = fila.get().createCell(2);
            celda.setCellValue("$".concat(ap.getProvisionIsn().toString()));
            celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV02"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión  de Vacaciones"));
            celda =fila.get().createCell(2);
                    celda.setCellValue("$".concat(ap.getProvisionVacaciones().toString()));
                    celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV03"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("IMSS Patronal"));
            celda = fila.get().createCell(2);
                    celda.setCellValue("$".concat(ap.getProvisionImss().toString()));
                    celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV04"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión de Prima Vacacional"));
            celda = fila.get().createCell(2);
                    celda.setCellValue("$".concat(ap.getProvisionPrimaVacacional().toString()));
                    celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV05"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión de aguinaldo"));
            celda = fila.get().createCell(2);
                    celda.setCellValue("$".concat(ap.getProvisionAguinaldo().toString()));
                    celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"SUB TOTAL");
            crearCeldaBordeEspecial(2,workbook,fila,"$" + UtileriasReporte.redondeoCantidad(String.valueOf(ap.getProvisionAguinaldo()+ap.getProvisionImss()+ap.getProvisionIsn()+ap.getProvisionVacaciones()+ap.getProvisionPrimaVacacional()),2));
            crearCeldaBordeEspecial(3,workbook,fila,"");

        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" contenidoAgrupadorPercepciones " + Constantes.ERROR_EXCEPCION, ex);
        }
    }



    private void contenidoAgrupadorDeducciones(AtomicInteger numFila,ReportePolizaContable ad,
                                                AtomicReference<XSSFRow> fila, XSSFWorkbook workbook,
                                               XSSFSheet hojaActual) throws ServiceException {
        try {

            numFila.getAndIncrement();
            AtomicReference<Double> suma = new AtomicReference<>(0.0);
            XSSFCellStyle estilo = workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.RIGHT);
            XSSFCell celda;
            for(SumaDeduciones c : ad.getAgrupadorDeducciones()){
                fila.set(hojaActual.createRow(numFila.get()));
                try {
                    fila.get().createCell(0)
                            .setCellValue(validaContenido(c.getCuentaContable()));
                    fila.get().createCell(1)
                            .setCellValue(validaContenido(c.getConceptoSat()));
                    fila.get().createCell(2)
                            .setCellValue("");
                    celda = fila.get().createCell(3);
                            celda.setCellValue("$" + validaContenido(c.getImporte().toString()));
                            celda.setCellStyle(estilo);

                    suma.updateAndGet(v -> v + c.getImporte());
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                numFila.getAndIncrement();
            }

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"SUB TOTAL");
            crearCeldaBordeEspecial(2,workbook,fila,"");
            crearCeldaBordeEspecial(3,workbook,fila,"$" + Precision.round(suma.get(),2));

            //Provisiones
            numFila.getAndIncrement();
            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV06"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Impuesto sobre nómina"));
            celda = fila.get().createCell(3);
                    celda.setCellValue("$".concat(ad.getProvisionIsn().toString()));
            celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV07"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión  de Vacaciones"));
            celda = fila.get().createCell(3);
                    celda.setCellValue("$".concat(ad.getProvisionVacaciones().toString()));
            celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV08"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("IMSS Patronal"));
            celda = fila.get().createCell(3);
                    celda.setCellValue("$".concat(ad.getProvisionImss().toString()));
            celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV09"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión de Prima Vacacional"));
            celda = fila.get().createCell(3);
                    celda.setCellValue("$".concat(ad.getProvisionPrimaVacacional().toString()));
            celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            fila.get().createCell(0)
                    .setCellValue(validaContenido("PROV10"));
            fila.get().createCell(1)
                    .setCellValue(validaContenido("Provisión de aguinaldo"));
            celda = fila.get().createCell(3);
                    celda.setCellValue("$".concat(ad.getProvisionAguinaldo().toString()));
                    celda.setCellStyle(estilo);

            fila.set(hojaActual.createRow(numFila.getAndIncrement()));
            crearCeldaBordeEspecial(0,workbook,fila,"");
            crearCeldaBordeEspecial(1,workbook,fila,"SUB TOTAL");
            crearCeldaBordeEspecial(3,workbook,fila,"$" + UtileriasReporte.redondeoCantidad(String.valueOf((ad.getProvisionAguinaldo()+ad.getProvisionImss()+ad.getProvisionIsn()+ad.getProvisionVacaciones()+ad.getProvisionPrimaVacacional())),2));
            crearCeldaBordeEspecial(2,workbook,fila,"");

        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" contenidoAgrupadorPercepciones " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private AtomicReference<Double> sumaPercepciones(List<SumaPercepcion> agrupadorPercepciones)
    throws ServiceException{
        try {
            AtomicReference<Double> suma = new AtomicReference<>(0.0);

            agrupadorPercepciones
                    .stream()
                    .forEach(c -> suma.updateAndGet(v -> v + Double.parseDouble(c.getImporte())));
            return suma;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" sumaPercepciones " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private AtomicReference<Double> sumaDeducciones(List<SumaDeduciones> agrupadorDeducciones)
            throws ServiceException{
        try {
            AtomicReference<Double> suma = new AtomicReference<>(0.0);
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.applyPattern(",##0.00");
            agrupadorDeducciones
                    .stream()
                    .forEach(c -> formatter.format(suma.updateAndGet(v -> v + c.getImporte())));
            return suma;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" sumaPercesumaDeduccionespciones " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private List<TablaBasicaDto> obtenInformacionProvisiones(List<PercepcionesDeduccionesAcumulados> listaProvisiones) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern(",##0.00");
        if (listaProvisiones != null) {
            listaProvisiones
                    .stream()
                    .forEach(c ->
                            lista.add(new TablaBasicaDto(c.getConceptoSat(), formatter.format(c.getImporte()))));
        } else {
            lista.add(new TablaBasicaDto("", ""));
        }
        return lista;
    }

    private List<TablaBasicaDto> obtenInformacionPercepciones(List<PercepcionesDeduccionesAcumulados> listaProvisiones) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern(",##0.00");
        if (listaProvisiones != null) {
            listaProvisiones
                    .stream()
                    .forEach(c ->
                            lista.add(new TablaBasicaDto(c.getConceptoSat(),formatter.format(c.getImporte()))));
        } else {
            lista.add(new TablaBasicaDto("", ""));
        }
        return lista;
    }





    private String valor(String valor,Double nuevo){
        valor = valor.trim().equalsIgnoreCase("")?"0":valor;
        Double sacar = Double.parseDouble(valor)+(nuevo==null?0:nuevo);
        return UtileriasReporte.redondeoCantidad(String.valueOf(sacar),2);
    }


}