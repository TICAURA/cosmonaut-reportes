package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.reportes.dto.SuaModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class ImssTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImssTest.class);

    @Inject
    @Client("/imss")
    public RxHttpClient cliente;

    @Test
    void testVariabilidad() {

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/variabilidad/" + 86),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
