package mx.com.ga.cosmonaut.reportes.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.orquestador.entity.PromedioVariables;
import mx.com.ga.cosmonaut.orquestador.service.VariabilidadService;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.promediovariables.PromedioVariableImpl;
import mx.com.ga.cosmonaut.reportes.services.ConexionClienteService;
import mx.com.ga.cosmonaut.reportes.services.ImssService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class ImssServiceImpl implements ImssService {

    @Inject
    private ConexionClienteService conexionClienteService;
    @Inject
    private VariabilidadService variabilidaPrd;



    @Override
    public RespuestaGenerica
    variabilidad(Integer variabilidad) throws ServiceException {
        try {

            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<PromedioVariables>  listaPromedioVariables = variabilidaPrd.obtenerPromedioVariables(variabilidad);

            if(!listaPromedioVariables.isEmpty()) {
                respuesta = this.reportePromedioVairables(listaPromedioVariables);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);

            }

            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" variabilidad " + Constantes.ERROR_EXCEPCION, ex);
        }
    }


    public RespuestaGenerica reportePromedioVairables(List<PromedioVariables> lista) {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        List<String> listaTitulos = Arrays.asList(
                "Número de empleado",
                "Nombre del empleado",
                "Sueldo bruto mensual",
                "Sueldo diario",
                "Factor integración",
                "Dias bimestre",
                "Dias laborados bimestre",
                "Ingresos variables",
                "Percepciones variables",
                "Salario base de cotización anterior",
                "Salario base de cotización actual",
                "Diferencia");
            Type collecPromedioVariables = new
                    TypeToken<List<PromedioVariableImpl>>(){}.getType();


        try{
            ByteArrayOutputStream salida =  llenarDatosVariabilidad(listaTitulos, lista);
            String base64 = Base64.getEncoder().encodeToString(salida.toByteArray());
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setDatos(base64);
        }catch (Exception e){
            respuesta.setMensaje(Constantes.ERROR);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
        }

        return respuesta;
    }

    private RespuestaGenerica consultaServicio(Integer variabilidad, Integer numServicio) throws ServiceException {
        try {
            return conexionClienteService.conectarClienteServicioImss
                    (variabilidad,numServicio);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" consultaServicio " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private String convierteMonto(Double monto){
        return  String.format("%,.2f", monto);
    }

    private ByteArrayOutputStream llenarDatosVariabilidad(List<String> listaTitulos, List<PromedioVariables> promedioVariables) throws ServiceException{
        try(ByteArrayOutputStream outFile = new ByteArrayOutputStream();
            XSSFWorkbook workbook = new XSSFWorkbook()) {

            workbook.createSheet("Promedio de Variables");
            XSSFSheet hojaActual = workbook.getSheetAt(0);

            unirCeldas(hojaActual,0,0,0,3);
            unirCeldas(hojaActual,1,1,0,3);
            unirCeldas(hojaActual,2,2,0,3);

            AtomicReference<XSSFRow> headerfila = new AtomicReference<>(hojaActual.createRow(0));

            /**Nombre Empresa */
            headerfila.get().createCell(0).setCellValue("Nombre de la empresa : " +
                    promedioVariables.get(0).getNombreEmpresa());

            headerfila.set(hojaActual.createRow(1));
            headerfila.get().createCell(0).setCellValue("Reporte de promedio de variables");

            /** Periodo: */
            headerfila.set(hojaActual.createRow(2));
            headerfila.get().createCell(0).setCellValue("Período: " + promedioVariables
                    .get(0).getPeriodo());
            /**Titulos dinamicos.*/
            AtomicInteger i = new AtomicInteger(0);
            headerfila.set(hojaActual.createRow(4));
            listaTitulos
                    .stream()
                    .forEach(a -> {
                        XSSFCell cell = headerfila.get().createCell(i.get());
                        cell.setCellValue(a);
                        cell.setCellStyle(crearEstiloCeldas(workbook));
                        hojaActual.setColumnWidth(i.get(),8000);
                        i.getAndIncrement();
                    });

            AtomicInteger j = new AtomicInteger(5);
            promedioVariables
                    .stream()
                    .forEach(c -> {

                        headerfila.set(hojaActual.createRow(j.getAndIncrement()));
                         generarColumnas(headerfila,0,c.getNumEmpleado());
                        generarColumnas(headerfila,1,String.format("%1$s %2$s %3$s",c.getNombre(),c.getApellidoPat(),c.getApellidoMat() == null ? "":c.getApellidoMat()));
                        generarColumnas(headerfila,2,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getSueldoBrutoMensual()),2))));
                        generarColumnas(headerfila,3,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getSalarioDiario()),2))));
                        generarColumnas(headerfila,4,redondeoCantidad(String.valueOf(c.getFactorIntegracion()),4));
                        generarColumnas(headerfila,5,String.valueOf(c.getDiasBimestre()));
                        generarColumnas(headerfila,6,String.valueOf(c.getDiasLaboradosBimestre()));
                        generarColumnas(headerfila,7,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getIngresosVariables()),2))));
                        generarColumnas(headerfila,8,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getPromedioVariable()),2))));
                        generarColumnas(headerfila,9,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getSbcAnterior()),2))));
                        generarColumnas(headerfila,10,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getSbcActual()),2))));
                        generarColumnas(headerfila,11,convierteMonto(Double.valueOf(redondeoCantidad(String.valueOf(c.getDiferencia()),2))));
                        });

            headerfila.set(hojaActual.createRow(j.get()));
            for (int k = 0; k < 12; k++) {
                crearCeldaBorde(k,workbook,headerfila,"");
                if (k == 1) {
                    crearCeldaBorde(k,workbook,headerfila,"Total de Empleados: " +
                            promedioVariables.get(0).getTotalEmpleados());
                }
            }

            workbook.write(outFile);

            return outFile;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" llenarDatosVariabilidad " + Constantes.ERROR_EXCEPCION, ex);
        }
    }


    public void generarColumnas(AtomicReference<XSSFRow> headerfila,int columna,String valor){
        XSSFCell cell = headerfila.get().createCell(columna);
        cell.setCellValue(valor);
    }
}
