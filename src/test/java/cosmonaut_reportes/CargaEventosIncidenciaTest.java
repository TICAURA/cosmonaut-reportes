package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.reportes.model.ReporteEventoIncidencia;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class CargaEventosIncidenciaTest {

    private static final Logger LOG = LoggerFactory.getLogger(CargaEventosIncidenciaTest.class);

    @Inject
    @Client("/cargaMasiva")
    public RxHttpClient cliente;

    @Test
    void testListarTodos() {
        ReporteEventoIncidencia reporteEventoIncidencia = new ReporteEventoIncidencia();
        reporteEventoIncidencia.setEsActivo(Boolean.TRUE);
        reporteEventoIncidencia.setIdEmpresa(470);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest
                                .POST("/layoutEventosIncidencias/", reporteEventoIncidencia),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
