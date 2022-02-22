package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.PagoComplementarioConsulta;
import mx.com.ga.cosmonaut.common.dto.reportes.PagoComplementario;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.PagoComplementarioRepository;
import mx.com.ga.cosmonaut.reportes.services.PagoComplementarioService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PagoComplementarioServiceImpl implements PagoComplementarioService {

    @Inject
    private PagoComplementarioRepository pagoComplementarioRepository;

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    private RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
    @Override
    public RespuestaGenerica listaDinamicaPagoComplementario(PagoComplementario pagoComplementario) throws ServiceException {
        try {
            List<PagoComplementarioConsulta> listaPagoComplementario = pagoComplementarioRepository
                    .consultaDimanica(pagoComplementario);
            respuestaGenerica.setDatos(listaPagoComplementario);
            respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " listaDinamicaPagoComplementario " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica empresasClientePrincipal(Integer clientePrincipal) throws ServiceException {
        try {

            respuestaGenerica.setDatos(empleadosReporteRepository
                    .empresasClientePrincipal(clientePrincipal));
            respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " empresasClientePrincipal " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica grupoNominaEmpresa(Integer idEmpresa) throws ServiceException {
        try {
            respuestaGenerica.setDatos(empleadosReporteRepository
                    .grupoNominaEmpresa(idEmpresa));
            respuestaGenerica.setResultado(mx.com.ga.cosmonaut.common.util.Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(mx.com.ga.cosmonaut.common.util.Constantes.EXITO);
            return respuestaGenerica;
        } catch (Exception ex) {
            throw new ServiceException(mx.com.ga.cosmonaut.common.util.Constantes.ERROR_CLASE +
                    this.getClass().getSimpleName()
                    + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_METODO +
                    " grupoNominaEmpresa " + mx.com.ga.cosmonaut.common.util.Constantes.ERROR_EXCEPCION, ex);
        }
    }


}
