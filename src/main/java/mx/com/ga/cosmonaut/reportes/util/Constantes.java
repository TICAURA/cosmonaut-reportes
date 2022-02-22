package mx.com.ga.cosmonaut.reportes.util;

import java.util.Arrays;
import java.util.List;



public final class Constantes {
    private Constantes() {}

    public static final String RUTA_CARPETA = "/Users/ramgm";
    //public static final String RUTA_CARPETA = "C:\\Users\\hitss\\Documents";
    public static final String RUTA_ARCHIVO_EMPLEADO = "/LayoutCargaMasivaEmpleados.xlsx";
    public static final String RUTA_ARCHIVO_EVENTOSINCIDENCIAS = "/LayoutCargaMasivaEventosIncidencias.xlsx";
    public static final String RUTA_ARCHIVO_EXEMPLEADO = "/LayoutCargaMasivaExEmpleados.xlsx";
    public static final String RUTA_ARCHIVO_EMPLEADOPPP = "/LayoutCargaMasivaEmpleadosPpp.xlsx";
    public static final String RUTA_JRXML_EMPLEADO = "/reportes/LayoutCargaMasivaEmpleado.jrxml";
    public static final String RUTA_JRXML_EXEMPLEADO = "/reportes/LayoutCargaMasivaExEmpleado.jrxml";
    public static final String RUTA_JRXML_EMPLEADOPPP = "/reportes/LayoutCargaMasivaEmpleadoPPP.jrxml";
    public static final String SEPARADOR = "-";
    public static final String RUTA_JRXML_EVENTO = "/reportes/LayoutCargaMasivaEvento.jrxml";
    public static final String RUTA_ARCHIVO_EVENTO = "/LayoutCargaMasivaEventos.xlsx";

    public static final String RUTA_ARCHIVO_PTU = "/layoutCargaPTUConReferencias.xlsx";
    public static final String RUTA_JRXML_PTU = "/reportes/LayoutCargaMasivaPTU.jrxml";
    public static final String RUTA_JRXML_DISPERSIONNOMINA = "/reportes/DispersionNomina.jrxml";
    public static final String RUTA_ARCHIVO_DISPERSIONNOMINA = "/DispersionNomina.xlsx";
    public static final String RUTA_ARCHIVO_POLIZA_CONTABLE = "/PolizaContable.xlsx";
    public static final String RUTA_ARCHIVO_NOMINAEXTRAORDINARIA = "/NominaExtraordinaria.xlsx";
    public static final String RUTA_ARCHIVO_REPORTENOMINA = "/ReporteNomina.xlsx";
    public static final String RUTA_ARCHIVO_PROMEDIOVARIABILIDAD = "/ReportePromedioVariabilidad.xlsx";
    public static final String RUTA_ARCHIVO_VIATICO = "/layoutCargaViaticos.xlsx";
    public static final String RUTA_JRXML_VIATICO = "/reportes/LayoutCargaMasivaViatico.jrxml";
    public static final String RUTA_ARCHIVO_NOMINAPPP = "/LayoutNominaPPP.xlsx";
    public static final String RUTA_ARCHIVO_NOMINAPPP_WEB = "/reportes/LayoutNominaPPP.xlsx";
    public static final String RUTA_JRXML_NOMINAPPP = "/reportes/LayoutCargaMasivaNominaPPP.jrxml";
    public static final String RUTA_ARCHIVO_LISTAEMPLEADO = "/ListaEmpleados.xlsx";
    public static final String RUTA_JRXML_LISTAEMPLEADO = "/reportes/LayoutCargaMasivaListaEmpleado.jrxml";
    public static final String RUTA_ARCHIVO_ACUMULADO_HISTORICA = "/AcumuladoNominaHistorica.xlsx";

    public static final List<Integer> LISTA_CELDA_VALOR_EMPLEADO =
            Arrays.asList(5, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29);
    public static final List<Integer> LISTA_CELDA_VALOR_EXEMPLEADO =
            Arrays.asList(5, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29);
    public static final List<Integer> LISTA_CELDA_VALOR_EMPLEADOPPP =
            Arrays.asList(5,13,14,15,18,19,20,21,22,23,24,28,29,30);

    /** lista para formato de columndas. */
    public static final List<Integer> LISTA_CAMPOS_NUMEROS_EMPLEADO = Arrays.asList(0,8,29,30,31);
    public static final List<Integer> LISTA_CAMPOS_NUMEROS_EXEMPLEADO = Arrays.asList(0,8,29,30,31);
    public static final List<Integer> LISTA_CAMPOS_NUMEROS_EMPLEADOPPP = Arrays.asList(0,8,30,31,32);
    public static final List<Integer> LISTA_CAMPOS_NUMEROS_PPP = Arrays.asList(4,5,7);
    public static final List<String> LISTA_CAMPOS_UNIDAD = Arrays.asList("1" + SEPARADOR + "Hora","2" + SEPARADOR + "Día",
            "3" + SEPARADOR + "Monto");


    public static final List<String> SEXO =
            Arrays.asList("M" + SEPARADOR + "Mujer", "H" + SEPARADOR + "Hombre");
    public static final List<String> DECISION =
            Arrays.asList("1" + SEPARADOR + "Si", "2" + SEPARADOR + "No");

    public static final String RUTA_JRXML_INCIDENCIA = "/reportes/LayoutCargaMasivaIncidencia.jrxml";
    public static final String RUTA_JRXML_EVENTOSINCIDENCIAS = "/reportes/LayoutCargaMasivaEventosIncidencias.jrxml";
    public static final String RUTA_ARCHIVO_INCIDENCIA = "/LayoutCargaMasivaIncidencias.xlsx";

    public static final List<String> LISTA_INCIDENCIA_HORAS_EXTRAS =
            Arrays.asList("1" + SEPARADOR + "Dobles","2"+ SEPARADOR +"Triples");
    public static final List<Integer> LISTA_CELDA_VALOR_INCIDENCIA = Arrays.asList(9,14);

    /** servicios. */
    public static final Integer CVE_SERVICIO_94 = 94;
    public static final Integer CVE_SERVICIO_23 = 23;
    public static final Integer CVE_SERVICIO_33 = 33;
    public static final Integer CVE_SERVICIO_39 = 39;
    public static final Integer CVE_SERVICIO_28 = 28;

    public static final String CFT_TRAB_SHEET = "Trabajadores";
    public static final String CFT_MOV_SHEET = "Movimientos";
    public static final String CFT_NSS_SHEET = "NSS";
    public static final String CFT_SDI_SHEET = "SDI";
    public static final String CFT_NOM_SHEET = "Nombre";
    public static final String CFT_AMOR_SHEET = "Amortización";
    public static final String CFT_RAMOR_SHEET = "ResumenAmortización";
    public static final String CFT_SDIP_SHEET = "SDIPresentar";

    public static final String CFT_TRAB_TITLE = "1) CORRESPONDENCIA DE TRABAJADORES ";
    public static final String CFT_MOV_TITLE = "2) CORRESPONDENCIA DE MOVIMIENTOS";
    public static final String CFT_NSS_TITLE = "3) Diferencia en NSS";
    public static final String CFT_SDI_TITLE = "4) DIFERENCIA EN SDI ";
    public static final String CFT_NOM_TITLE = "5) DIFERENCIA EN NOMBRE";
    public static final String CFT_AMOR_TITLE = "6) DIFERENCIA EN TIPO DE DESCUENTO O NUMERO CREDITO";
    public static final String CFT_RAMOR_TITLE = "7) RESUMEN DIFERENCIA EN TIPO DE DESCUENTO O NUMERO CREDITO";
    public static final String CFT_SDIP_TITLE = "8) DIFERENCIA EN SDI";

    public static final String CFT_TRAB_SUB1 = "Trabajadores en el IMSS y no en la Empresa";
    public static final String CFT_TRAB_SUB2 = "Trabajadores en la Empresa y no en el IMSS";
    public static final String CFT_MOV_SUB1 = "Movimientos que no existen en Cosmonaut";
    public static final String CFT_MOV_SUB2 = "Movimientos que no existen en la Emisión";
    public static final String CFT_SDI_SUB1 = "a) Salario integrado Cosmonaut menor a Emisión";
    public static final String CFT_SDI_SUB2 = "b) Salario integrado Cosmonaut mayor a Emisión";
    public static final String CFT_RAMOR_SUB1 = "a) Amortizaciones con Diferencias";
    public static final String CFT_RAMOR_SUB2 = "b) Amortizaciones sin Diferencia";

    public static final String CFT_REGPAT_CELL = "Registro patronal";
    public static final String CFT_IDCTE_CELL = "Id de Cliente";
    public static final String CFT_CTE_CELL = "Cliente";
    public static final String CFT_NOEMP_CELL = "Número de empleado";
    public static final String CFT_NSS_CELL = "NSS";
    public static final String CFT_NOMTRAB_CELL = "Nombre del trabajador";
    public static final String CFT_MOV_CELL = "Movimiento";
    public static final String CFT_FECHAMOV_CELL = "Fecha Movimiento";
    public static final String CFT_SDI_CELL = "Integrado";
    public static final String CFT_DIF_CELL = "Diferencia";
    public static final String CFT_NCRE_CELL = "N Credito";
    public static final String CFT_FECHAIN_CELL = "Fecha inicio";
    public static final String CFT_TIPODES_CELL = "Tipo descuento";
    public static final String CFT_DIAS_CELL = "Dias";
    public static final String CFT_INC_CELL = "Inc";
    public static final String CFT_AUS_CELL = "Aus";
    public static final String CFT_FACTOR_CELL = "Factor";
    public static final String CFT_IMPORTE_CELL = "Importe";

    public static final Integer CVE_SERVICIO_117 = 117;
    public static final Integer CVE_SERVICIO_115 = 115;
    public static final Integer CVE_SERVICIO_119 = 119;
    public static final Integer CVE_SERVICIO_135 = 135;
    public static final Integer CVE_SERVICIO_116 = 116;
    public static final Integer CVE_SERVICIO_118 = 118;

    public static final String IDSE_CONSULTA = "/IDSEConsulta";
    public static final String SUA_ALTAS = "/SUAAltas";
    public static final String SUA_MODIFICACION = "/SUAModificacion";
    public static final Integer ES_UNO = 1;
    public static final Integer ES_DOS = 2;
    public static final Integer ES_TRES = 3;
    public static final Integer ES_CUATRO = 4;
    public static final String PDF = ".pdf";
    public static final String PDF_COMPROBANTE_FISCAL = "/ComprobanteFiscal";
    public static final String ZIP = ".zip";
    public static final String XML = ".xml";
    public static final String REPORTE_RAYA_HISTORICA = "ReporteRayaHistorica";
    public static final String REPORTE_NOMINA_HISTORICA = "NominaHistorica";

}
