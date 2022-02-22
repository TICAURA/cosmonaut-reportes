package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.SuaAltasConsulta;
import mx.com.ga.cosmonaut.common.dto.consultas.SuaModificacionConsulta;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.dto.SuaModel;
import mx.com.ga.cosmonaut.reportes.services.SuaService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.*;

@Singleton
public class SuaServiceImpl implements SuaService {

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();

    @Override
    public RespuestaGenerica suaAltas(SuaModel suaModel) throws ServiceException {
        try {
            //Se agrega a la consulta el valor correcto
            List<SuaAltasConsulta> suaAltasConsultas = consultaSuaAltas(suaModel.getIdEmpresa());

            suaAltasConsultas = suaAltasConsultas
                    .stream()
                    .filter(c -> suaModel.getIdKardex().contains(c.getKardexId()))
                    .collect(Collectors.toList());

            respuestaGenerica.setDatos(escribirArchivoAltas(SUA_ALTAS + suaModel.getIdEmpresa(),
                    suaAltasConsultas));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);

            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " suaAltas " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica suaModificacion(SuaModel suaModel) throws ServiceException {
        try {
            List<SuaModificacionConsulta> suaModificacionConsultas =
                    consultaSuaModificacion(suaModel.getIdEmpresa());
            suaModificacionConsultas = suaModificacionConsultas
                    .stream()
                    .filter(c -> suaModel.getIdKardex().contains(c.getKardexId()))
                    .collect(Collectors.toList());

            respuestaGenerica.setDatos(escribirArchivoModificacion(SUA_MODIFICACION + suaModel.getIdEmpresa(),
                    suaModificacionConsultas));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);

            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " suaModificacion " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private List<SuaAltasConsulta> consultaSuaAltas(Integer idEmpresa) throws ServiceException{
        try {
            return empleadosReporteRepository.suaAltas(idEmpresa);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaSuaAltas " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private String escribirArchivoAltas(String nombreArchivo, List<SuaAltasConsulta> suaAltasConsultas)
            throws ServiceException {

        File archivoTxt = new File( RUTA_CARPETA + nombreArchivo + ".txt");

        try (FileWriter escribir = new FileWriter(archivoTxt, false)) {

            suaAltasConsultas.stream()
                    .forEach( c -> {
                        try {
                            escribir.write(validaCadenaComplementaria(c.getRegistroPatronal(),11,1,0));
                            escribir.write(validaCadenaComplementaria(c.getNss().toUpperCase(),11,1,0));
                            escribir.write(validaCadenaComplementaria(c.getRfc().toUpperCase(),13,1,0));
                            escribir.write(validaCadenaComplementaria(c.getCurp().toUpperCase(),18,1,0));
                            String nombre= c.getApellidoPat()+"$"+c.getApellidoMat()+"$"+c.getNombreEmpleado();
                            if(c.getApellidoMat()==null || c.getApellidoMat().isEmpty() || c.getApellidoMat().toUpperCase().equals("NULL"))
                                nombre= c.getApellidoPat()+"$"+c.getNombreEmpleado();
                            if (c.getApellidoPat()==null || c.getApellidoPat().isEmpty() || c.getApellidoPat().toUpperCase().equals("NULL"))
                                nombre= c.getApellidoMat()+"$$"+c.getNombreEmpleado();
                            escribir.write(validaCadenaComplementaria(nombre.toUpperCase(),50,1,0));
                            escribir.write(validaCadenaComplementaria(c.getTipoTrabajador(),1,1,0));
                            escribir.write(validaCadenaComplementaria("0",1,1,0));//tipo de jornada c.getJornadaReducida()
                            Date date1=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(c.getFechaMovimiento());
                            Calendar day=Calendar.getInstance();
                            day.setTimeInMillis(date1.getTime());
                            SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy");
                            format1.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                            String formatted = format1.format(day.getTime());
                            escribir.write(validaCadenaComplementaria(formatted,8,1,0));
                            escribir.write(validaCadenaComplementaria(
                                    Boolean.TRUE.equals(validaCadena(c.getSdi())) ? c.getSdi().replace(".","") : "0",7,0,1));
                            escribir.write(validaCadenaComplementaria(c.getClaveSubdelegacion(),17,1,0));
                            escribir.write(validaCadenaComplementaria(c.getCredInfonavit(),10,1,0));
                            escribir.write(validaCadenaComplementaria(c.getInicioDescuento(),8,1,0));
                            escribir.write(validaCadenaComplementaria(c.getTipoDescuento(),1,1,0));
                            escribir.write(validaCadenaComplementaria(c.getValorDescuento(),8,1,1));
                            escribir.write("\n");
                        } catch (IOException | ServiceException | ParseException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " escribirArchivoAltas " + Constantes.ERROR_EXCEPCION, ex);
        }
        return convertiraBase64(nombreArchivo + ".txt");
    }

    private List<SuaModificacionConsulta> consultaSuaModificacion(Integer idEmpresa) throws ServiceException{
        try {
            return empleadosReporteRepository.suaModificacion(idEmpresa);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaSuaModificacion " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private String escribirArchivoModificacion(String nombreArchivo, List<SuaModificacionConsulta> suaModificacionConsultas)
            throws ServiceException {

        File archivoTxt = new File( RUTA_CARPETA + nombreArchivo + ".txt");

        try (FileWriter escribir = new FileWriter(archivoTxt, false)) {

            suaModificacionConsultas.stream()
                    .forEach( c -> {
                        try {
                            escribir.write(validaCadenaComplementaria(c.getRegistroPatronal(),11,1,0));
                            escribir.write(validaCadenaComplementaria(c.getNss(),11,1,0));
                            escribir.write(validaCadenaComplementaria(c.getTipoMovimiento(),2,0,1));
                            System.out.println("fecha movimiento "+c.getFechaMovimiento());
                            Date date1=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(c.getFechaMovimiento());
                            Calendar day=Calendar.getInstance();
                            day.setTimeInMillis(date1.getTime());
                            SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy");
                            format1.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                            String formatted = format1.format(day.getTime());
                            System.out.println("fecha movimiento format "+formatted);
                            escribir.write(validaCadenaComplementaria(formatted,8,1,0));
                            escribir.write(validaCadenaComplementaria(c.getFillerBlancoOcho(),8,1,0));
                            escribir.write(validaCadenaComplementaria(c.getNumeroDias(),2,1,1));
                            escribir.write(validaCadenaComplementaria(c.getSbc().replace(".",""),7,0,1));
                            escribir.write("\n");
                        } catch (IOException | ServiceException | ParseException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " escribirArchivoModificacion " + Constantes.ERROR_EXCEPCION, ex);
        }
        return convertiraBase64(nombreArchivo + ".txt");
    }
}
