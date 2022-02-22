package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.reportes.model.ReporteIncidenciaModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class CargaIncidenciaTest {

    private static final Logger LOG = LoggerFactory.getLogger(CargaIncidenciaTest.class);

    @Inject
    @Client("/cargaMasiva")
    public RxHttpClient cliente;

    @Test
    void testListarTodos() {
        ReporteIncidenciaModel incidenciaModel = new ReporteIncidenciaModel();
        List<Integer> listaIdEmpleados = Arrays.asList(563,564,565);
        incidenciaModel.setIdEmpresa(339);
        incidenciaModel.setListaIdEmpleados(listaIdEmpleados);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/layoutIncidencia/", incidenciaModel),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
