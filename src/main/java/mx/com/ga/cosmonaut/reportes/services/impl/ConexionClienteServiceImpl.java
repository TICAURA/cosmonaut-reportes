package mx.com.ga.cosmonaut.reportes.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Cliente;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.dto.Imss;
import mx.com.ga.cosmonaut.reportes.dto.Nomina;
import mx.com.ga.cosmonaut.reportes.dto.respuesta.NominaRespuesta;
import mx.com.ga.cosmonaut.reportes.services.ConexionClienteService;
import mx.com.ga.cosmonaut.reportes.util.UtileriasReporte;
import okhttp3.*;
import org.json.JSONObject;

import javax.inject.Singleton;
import java.util.Objects;

import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.generaRespuesta;

@Singleton
public class ConexionClienteServiceImpl implements ConexionClienteService {

    @Value("${servicio.cliente.host}")
    private String host;

    @Value("${servicio.cliente.path}")
    private String contexto;

    private static final String CLABE = "?cve=";

    @Override
    public RespuestaGenerica conectarClienteServicio(Integer nominaXperiodoId, Integer cveServicio) throws ServiceException{
        Nomina nominaObject = new Nomina(nominaXperiodoId,"",0,0,0);
        try {
            return generaRespuesta(cliente(nominaObject,cveServicio));
        }catch (Exception e){
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE + UtileriasReporte.class.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " conectarClienteServicio " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica conectarClienteServicio(Object object, Integer cveServicio) throws ServiceException{
        try {
            return generaRespuesta(cliente(object,cveServicio));
        }catch (Exception e){
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE + UtileriasReporte.class.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " conectarClienteServicio " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica conectarClienteServicioImss(Integer variabilidad, Integer cveServicio) throws ServiceException {
        Imss imss = new Imss(variabilidad);
        try {
            return generaRespuesta(cliente(imss,cveServicio));
        }catch (Exception e){
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE + UtileriasReporte.class.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO + " conectarClienteServicioImss " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, e);
        }
    }

    private NominaRespuesta cliente(Object objetoSolicitud, Integer clabe) throws ServiceException {
        try{
            final MediaType media = MediaType.get("application/json; charset=utf-8");
            OkHttpClient cliente = Cliente.obtenOkHttpCliente();
            cliente.sslSocketFactory();
            JSONObject json = new JSONObject(objetoSolicitud);
            RequestBody cuerpoSolicitud = RequestBody.create(json.toString(),media);
            Request solicitud = new Request.Builder()
                    .url(host + contexto + CLABE + clabe)
                    .post(cuerpoSolicitud)
                    .build();
            Call llamada = cliente.newCall(solicitud);
            Response respuesta = llamada.execute();
            if (respuesta.isSuccessful()){
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(Objects.requireNonNull(respuesta.body()).string(), NominaRespuesta.class);
            }
            return new NominaRespuesta();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " cliente " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
