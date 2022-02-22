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
class SuaTest {

    private static final Logger LOG = LoggerFactory.getLogger(SuaTest.class);

    @Inject
    @Client("/sua")
    public RxHttpClient cliente;

    @Test
    void testSuaAltas() {
        SuaModel idseModel = new SuaModel();
        idseModel.setIdEmpresa(482);
        idseModel.setIdKardex(Arrays.asList(38,39,40));

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/altas",idseModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testSuaModificacion() {
        SuaModel idseModel = new SuaModel();
        idseModel.setIdEmpresa(482);
        idseModel.setIdKardex(Arrays.asList(38,39,40));

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificacion",idseModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

}
