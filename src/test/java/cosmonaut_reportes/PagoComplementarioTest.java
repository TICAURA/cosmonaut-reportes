package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.reportes.PagoComplementario;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class PagoComplementarioTest {

    private static final Logger LOG = LoggerFactory.getLogger(PagoComplementarioTest.class);

    @Inject
    @Client("/pagoComplementario")
    public RxHttpClient cliente;



    void testListaDinamicaPagoComplementario() {
        PagoComplementario pagoComplementario = new PagoComplementario();
        pagoComplementario.setNumeroEmpleado("");
        pagoComplementario.setNombreEmpleado("");
        pagoComplementario.setPrimerApellidoEmpleado("");
        pagoComplementario.setSegundoApellidoEmpleado("");
        pagoComplementario.setGrupoNomina(0);
        pagoComplementario.setIdEmpresa(470);
        pagoComplementario.setIdEmpresaActual(0);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/filtroDinamico",pagoComplementario),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }


    @Test
    void testEmpresasClientePrincipalo() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/empresas/" + 112),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testGrupoNominaEmpresa() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/grupoNomina/" + 470),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

}
