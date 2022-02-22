package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.IdseModel;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class IdseTest {

    private static final Logger LOG = LoggerFactory.getLogger(IdseTest.class);

    @Inject
    @Client("/idse")
    public RxHttpClient cliente;

    @Test
    void testIdseConsulta() {
        IdseModel idseModel = new IdseModel();
        idseModel.setIdEmpresa(482);
        idseModel.setIdKardex(Arrays.asList(38,39,40));
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/consulta",idseModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testIdseConsultaCentroClientePadre() {
        IdseModel idseModel = new IdseModel();
        idseModel.setIdEmpresa(460);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/consultaCentroCostoPadre",idseModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
