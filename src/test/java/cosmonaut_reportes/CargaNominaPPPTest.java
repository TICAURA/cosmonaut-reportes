package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class CargaNominaPPPTest {

    private static final Logger LOG = LoggerFactory.getLogger(CargaNominaPPPTest.class);

    @Inject
    @Client("/cargaMasiva")
    public RxHttpClient cliente;

    @Test
    void testListarTodos() {
        ReporteIncidenciaModel reporteIncidenciaModel = new ReporteIncidenciaModel();
        reporteIncidenciaModel.setIdEmpresa(470);

        reporteIncidenciaModel.setListaIdEmpleados(Arrays.asList(924,901,919,920,921,923));
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutNominaPPP/",reporteIncidenciaModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
