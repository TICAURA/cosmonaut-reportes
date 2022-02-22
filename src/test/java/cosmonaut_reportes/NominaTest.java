package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.reportes.model.ComprobanteFiscalModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class NominaTest {

    private static final Logger LOG = LoggerFactory.getLogger(NominaTest.class);

    @Inject
    @Client("/nominaDispersion")
    public RxHttpClient cliente;

    @Test
    void testListarTodos() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/txtIdRfc/"+1203),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testLayoutDispersionNomina() {
        ComprobanteFiscalModel fiscalModel = new ComprobanteFiscalModel();
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutDispersionNomina/",fiscalModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testLayoutNominaExtraordinaria() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/layoutNominaExtraordinaria/" + 339),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testLayoutNominaOrdinaria() {
        ComprobanteFiscalModel fiscalModel = new ComprobanteFiscalModel();
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutReporteNomina/", fiscalModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
