package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class CargaEventoTest {

    private static final Logger LOG = LoggerFactory.getLogger(CargaEventoTest.class);

    @Inject
    @Client("/cargaMasiva")
    public RxHttpClient cliente;

    @Test
    void testListarTodos() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/layoutEvento"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
