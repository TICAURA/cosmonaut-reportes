package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.model.TipoCarga;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class CargaEmpleadosTest {

    private static final Logger LOG = LoggerFactory.getLogger(CargaEmpleadosTest.class);

    @Inject
    @Client("/cargaMasiva")
    public RxHttpClient cliente;

    private TipoCarga tipoCarga = new TipoCarga();

    @Test
    void testLayoutEmpleado() {
        tipoCarga.setIdEmpresa(339);
        tipoCarga.setTipoCargaId(1);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutEmpleado/",tipoCarga),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testLayoutExEmpleado() {
        tipoCarga.setIdEmpresa(339);
        tipoCarga.setTipoCargaId(2);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutEmpleado/",tipoCarga),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testLayoutEmpleadoPPP() {
        tipoCarga.setIdEmpresa(339);
        tipoCarga.setTipoCargaId(3);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutEmpleado/",tipoCarga),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testListarEmpleados() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/layoutListaEmpleado/" + 470),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

}
