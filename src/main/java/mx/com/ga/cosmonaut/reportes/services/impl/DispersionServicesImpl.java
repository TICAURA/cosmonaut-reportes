package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.DispersionConsultaError;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.DispersionTimbradoRepository;
import mx.com.ga.cosmonaut.common.service.ReporteService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.services.DispersionServices;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Singleton
public class DispersionServicesImpl implements DispersionServices {

    @Inject
    private ReporteService reporteService;

    @Inject
    private DispersionTimbradoRepository dispersionTimbradoRepository;

    @Override
    public RespuestaGenerica generaListaEmpleadosDispersion(Integer nominaPeriodoId, List<Integer> listaPersonas) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/ErroresDispersion.jrxml");
            HashMap<String, Object> parametros = new HashMap<>();

            List<DispersionConsultaError> lista = dispersionTimbradoRepository.consultaDispersionEmpleadosErrores(nominaPeriodoId,listaPersonas);
            JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(lista);
            JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros,datos);
            String[] nombreHojas = new String[]{"Dispersion Erroneos"};
            respuesta.setDatos(reporteService.generaReporteExcel(reporte,nombreHojas));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" generaListaEmpleadosDispersion " + Constantes.ERROR_EXCEPCION, ex);
        }
    }
}
