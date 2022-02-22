package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrTimbre;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrTimbreRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.orquestador.service.ComprobanteFiscalServices;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;
import mx.com.ga.cosmonaut.reportes.services.ComprobanteFiscalService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static mx.com.ga.cosmonaut.reportes.util.Constantes.*;

@Singleton
public class ComprobanteFiscalServiceImpl implements ComprobanteFiscalService {

    @Inject
    private ComprobanteFiscalServices repo;

    @Inject
    private NcrTimbreRepository ncrTimbreRepository;

    @Override
    public RespuestaGenerica comprobanteFiscal(ComprobanteFiscalModel comprobanteFiscalModel,Integer cvServicio) throws ServiceException {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(!comprobanteFiscalModel.isEsZip()){
                Optional<NcrTimbre> timbre = this.consultaTimbre(comprobanteFiscalModel.getNominaPeriodoId(),comprobanteFiscalModel.getClienteId(),Integer.parseInt(comprobanteFiscalModel.getIdEmpleado()),true);
                String base = this.repo.obtenerComprobanteFiscal(comprobanteFiscalModel.getNominaPeriodoId(),Integer.parseInt(comprobanteFiscalModel.getIdEmpleado()),(timbre.isPresent())?timbre.get():null,cvServicio==28);
                respuesta.setDatos(base);
            }else{

                String  base;
                if(comprobanteFiscalModel.getIdEmpleado() == null){
                     base = this.repo.obtenerComprobanteFiscal(comprobanteFiscalModel.getNominaPeriodoId(),cvServicio==28);
                }else{
                    base = this.repo.obtenerComprobanteFiscalByEmpleado(comprobanteFiscalModel.getNominaPeriodoId(),Integer.parseInt(comprobanteFiscalModel.getIdEmpleado()),cvServicio==28);;
                }
                respuesta.setDatos(base);
            }
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(true);
            return respuesta;
    }

    private Boolean crearArchivoZip(List<String> nombreArchivoRFC, String nombreZip, Boolean esTimbrado) throws ServiceException {
        /**creacion de archivo zip.*/
        byte[] buffer = new byte[2048];

        try (FileOutputStream fos = new FileOutputStream(RUTA_CARPETA + nombreZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            nombreArchivoRFC.stream().forEach(c -> {
                ZipEntry ze = new ZipEntry(c + PDF);
                ZipEntry zeXml = new ZipEntry(c + XML);
                try (FileInputStream pdf =
                             new FileInputStream(RUTA_CARPETA + PDF_COMPROBANTE_FISCAL + c + PDF);
                     FileInputStream xml =
                             new FileInputStream(RUTA_CARPETA + PDF_COMPROBANTE_FISCAL + c + XML)) {
                    zos.putNextEntry(ze);
                    int len;
                    while ((len = pdf.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    if (Boolean.FALSE.equals(esTimbrado)) {
                        zos.putNextEntry(zeXml);
                        int ln;
                        while ((ln = xml.read(buffer)) > 0) {
                            zos.write(buffer, 0, ln);
                        }
                    }
                } catch (IOException  e) {
                    e.printStackTrace();
                }
            });
            zos.closeEntry();
            return true;
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " crearArchivoZip " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private Optional<NcrTimbre> consultaTimbre(Integer nominaPeriodoId, Integer centrocClienteId, Integer personaId, boolean esActual) throws ServiceException {
        try {
            return ncrTimbreRepository.findByNominaPeriodoIdAndCentrocClienteIdAndPersonaIdAndEsActual(
                    /**494,458,785,true);*/
                    nominaPeriodoId,centrocClienteId,personaId,esActual);
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaTimbre " + Constantes.ERROR_EXCEPCION, e);
        }
    }



}
