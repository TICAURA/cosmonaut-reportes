package cosmonaut_reportes;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.reportes.model.AcumuladoConcepto;
import mx.com.ga.cosmonaut.reportes.model.FolioFiscal;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class NominaHistoricaTest {

    private static final Logger LOG = LoggerFactory.getLogger(NominaHistoricaTest.class);

    @Inject
    @Client("/nominaHistorica")
    public RxHttpClient cliente;

    @Test
    void testPolizaContable() {

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/polizaContable/" + 602),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testReporteRaya() {

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/reporteRaya/" + 177),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testAcumuladoConcepto() {
        AcumuladoConcepto acumuladoConcepto = new AcumuladoConcepto();
        acumuladoConcepto.setMes(5);
        acumuladoConcepto.setClienteId(463);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/acumuladoConcepto" ,acumuladoConcepto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testFolioFiscal() {
        FolioFiscal folioFiscal = new FolioFiscal();
        folioFiscal.setNominaXperiodoId(494);
        folioFiscal.setListaIdPersona(Arrays.asList(785,856,857,862));
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/folioFiscal" ,folioFiscal),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    void testDetalleNominaHistorica() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/detalleNominaHistorica/" + 653),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
