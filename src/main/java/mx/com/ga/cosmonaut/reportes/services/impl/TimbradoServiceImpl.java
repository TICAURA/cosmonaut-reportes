package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.TimbradoErroresConsulta;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.TimbradoRepository;
import mx.com.ga.cosmonaut.common.service.ReporteService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.services.TimbradoService;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Singleton
public class TimbradoServiceImpl implements TimbradoService {

    @Inject
    private ReporteService reporteService;

    @Inject
    private TimbradoRepository timbradoRepository;

    @Override
    public RespuestaGenerica listaErrores(Integer nominaPeriodoId,List<Integer> listaPersonas) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/ErroresTimbrado.jrxml");
            HashMap<String, Object> parametros = new HashMap<>();
            List<TimbradoErroresConsulta> lista = timbradoRepository.consultaListaErrores(nominaPeriodoId,listaPersonas);
            JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(lista);
            JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros,datos);
            String[] nombreHojas = new String[]{"Errores"};
            respuesta.setDatos(reporteService.generaReporteExcel(reporte,nombreHojas));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaErrores " + Constantes.ERROR_EXCEPCION, ex);
        }
    }
}
