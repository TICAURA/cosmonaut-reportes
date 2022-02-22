package mx.com.ga.cosmonaut.reportes.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGoogleStorage;
import mx.com.ga.cosmonaut.common.dto.consultas.DomicilioConsulta;
import mx.com.ga.cosmonaut.common.dto.reportes.EmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.reportes.EmpleadoRecuentoDto;
import mx.com.ga.cosmonaut.common.dto.reportes.TablaBasicaDto;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatParentesco;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatNacionalidad;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatParentescoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatNacionalidadRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclSedeRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.ContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.DomicilioRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.service.ReporteService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ConstantesReportes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.orquestador.entity.BeneficiosPolitica;
import mx.com.ga.cosmonaut.orquestador.repository.PoliticaRepository;
import mx.com.ga.cosmonaut.reportes.services.EmpleadoService;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.YEARS;
import static mx.com.ga.cosmonaut.reportes.util.UtileriasReporte.validaDato;

@Singleton
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final String PARAMETRO_EMPRESA = "pEmpresa";

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private ReporteService reporteService;

    @Inject
    private ContratoColaboradorRepository contratoColaboradorRepository;

    @Inject
    private DomicilioRepository domicilioRepository;

    @Inject
    private CatParentescoRepository catParentescoRepository;

    @Inject
    private CatNacionalidadRepository catNacionalidadRepository;

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private GoogleStorageService googleStorageService;

    @Inject
    private NclSedeRepository nclSedeRepository;

    @Inject
    private PoliticaRepository politicaRepository;

    @Override
    public RespuestaGenerica generaPerfilPersonal(NcoContratoColaborador colaborador) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            InputStream imagenPerfil = this.getClass().getResourceAsStream("/reportes/perfil.png");
            InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/PerfilEmpleado.jrxml");
            HashMap<String, Object> parametros = obtenParametros(colaborador, imagenPerfil);
            JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros);
            respuesta.setDatos(reporteService.generaReportePDF(reporte));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            //reporteService.generaReportePdfRuta(reporte, "C:\\Users\\Usuario\\Desktop\\ASG\\perfilEmpleado.pdf");
            //reporteService.generaReportePdfRuta(reporte, "C:\\reportes\\layoutCargaMasiva\\perfilEmpleado.pdf");
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" generaPerfilPersonal " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica generaRecuentoEmpleados(Long id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/ListaRecuentoEmpleados.jrxml");
            HashMap<String, Object> parametros = new HashMap<>();
            NclCentrocCliente empresa = nclCentrocClienteRepository.findById(id.intValue()).orElse(new NclCentrocCliente());
            parametros.put(PARAMETRO_EMPRESA,empresa.getRazonSocial());
            List<NcoContratoColaborador> listaContratoColaborador =
                    ncoContratoColaboradorRepository.findByCentrocClienteIdCentrocClienteId(id.intValue());
            List<EmpleadoRecuentoDto> listaEmpleados = obtenListaEmpleados(listaContratoColaborador);

            JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros,new JRBeanCollectionDataSource(listaEmpleados));
            String[] nombreHojas = new String[]{ ConstantesReportes.FILA_ALERGIAS};
            respuesta.setDatos(reporteService.generaReporteExcel(reporte,nombreHojas));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" generaRecuentoEmpleados " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    @Override
    public RespuestaGenerica generaListaEmpleados(Long id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            InputStream reporteJasper = this.getClass().getResourceAsStream("/reportes/ListaEmpleado.jrxml");
            NclCentrocCliente empresa = nclCentrocClienteRepository.findById(id.intValue()).orElse(new NclCentrocCliente());
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put(PARAMETRO_EMPRESA,empresa.getRazonSocial());
            parametros.put("pNombreRepote","Reporte de Empleados");
            parametros.put("pRfc","Registro RFC: " +empresa.getRfc());
            List<EmpleadoDto> lista = contratoColaboradorRepository.consultaListaEmpleado(id.intValue());
            JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(lista);
            JasperPrint reporte = reporteService.generaJasper(reporteJasper,parametros,datos);
            String[] nombreHojas = new String[]{ ConstantesReportes.FILA_ALERGIAS};
            respuesta.setDatos(reporteService.generaReporteExcel(reporte,nombreHojas));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" generaListaEmpleados " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private HashMap<String, Object> obtenParametros(NcoContratoColaborador colaborador, InputStream imagenPerfil) throws ServiceException {
        HashMap<String, Object> parametros = new HashMap<>();

        NcoContratoColaborador contratoColaborador = ncoContratoColaboradorRepository.findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                colaborador.getFechaContrato(),
                colaborador.getPersonaId().getPersonaId(),
                colaborador.getCentrocClienteId().getCentrocClienteId()
        ).orElseThrow(() -> new ServiceException("No se encontro el empleado"));

        if(contratoColaborador != null){
            NcoPersona persona = contratoColaborador.getPersonaId();
            NclCentrocCliente empresa = contratoColaborador.getCentrocClienteId();

            String puestoEmpleado = contratoColaborador.getPuestoId().getNombreCorto().toUpperCase().charAt(0) +
                    contratoColaborador.getPuestoId().getNombreCorto().substring(1);

            parametros.put("pTitulo",ConstantesReportes.TITULO_PERFIL_PERSONAL);
            parametros.put(PARAMETRO_EMPRESA,empresa.getRazonSocial());
            parametros.put("pFechaFormato","Descarga del formato: " + Utilidades.fechaTexto(Utilidades.obtenerFechaSystema()));
            parametros.put("pNombre",persona.getNombre() + " " + persona.getApellidoPaterno() + " " + Utilidades.validaString(persona.getApellidoMaterno()));
            parametros.put("pArea",contratoColaborador.getAreaId().getDescripcion());
            parametros.put("pPuesto",puestoEmpleado);
            parametros.put("pTelefono", validaDato(persona.getContactoInicialTelefono()));

            RespuestaGoogleStorage respuestaGoogle;

            InputStream imagenImagen;
            if (persona.getUrlImagen() != null && !persona.getUrlImagen().isEmpty()){
                respuestaGoogle = googleStorageService.obtenerArchivo(persona.getUrlImagen());
                imagenImagen = new ByteArrayInputStream(respuestaGoogle.getArreglo());
                parametros.put("pImagen",imagenImagen);
            }else {
                parametros.put("pImagen",imagenPerfil);
            }

            InputStream imagenLogo = null;
            if (empresa.getUrlLogo() != null && !empresa.getUrlLogo().isEmpty()){
                respuestaGoogle = googleStorageService.obtenerArchivo(empresa.getUrlLogo());
                imagenLogo = new ByteArrayInputStream(respuestaGoogle.getArreglo());
            }

            InputStream logoCosmonaut = this.getClass().getResourceAsStream("/reportes/logoCosmonaut.png");

            parametros.put("pLogoCosmonaut",logoCosmonaut);
            parametros.put("pLogo",imagenLogo);
            parametros.put("pTituloInformacion",ConstantesReportes.TITULO_INFORMACION_PERSONAL);
            parametros.put("pDatosInformacion",obtenInformacionPersonal(persona));
            parametros.put("pTituloDomicilio",ConstantesReportes.TITULO_DOMICILIO);
            parametros.put("pDomicilio",obtenDomicilio(persona.getPersonaId()));
            parametros.put("pTituloDetalleEmpleo",ConstantesReportes.TITULO_INFORMACION_PUESTO);
            parametros.put("pDetalleEmpleo",obtenDetallePuesto(contratoColaborador));
            parametros.put("pTituloDetalleCompensacion",ConstantesReportes.TITULO_COMPENSACION);
            parametros.put("pDetalleCompensacion",obtenCompensacion(contratoColaborador));
            parametros.put("pTituloInformacionAdicional",ConstantesReportes.TITULO_INFORMACION_ADICIONAL);
            parametros.put("pInformacionAdicional",obtenInformacionAdicional(persona));
            parametros.put("pTituloContacto",ConstantesReportes.TITULO_CONTACTO_EMERGENCIA);
            parametros.put("pDatosContacto",obtenInformacionContactoEmergencia(persona));
            parametros.put("pTituloPreferencias",ConstantesReportes.TITULO_PREFERENCIAS);
            parametros.put("pPreferencias",obtenInformacionPreferencias());
            parametros.put("pTituLoInformacionContrato",ConstantesReportes.TITULO_INFORMACION_CONTRATO);
            parametros.put("pInformacionContrato",obtenInformacionContrato(contratoColaborador, empresa));
        }

        return parametros;
    }

    private List<TablaBasicaDto> obtenInformacionPersonal(NcoPersona persona) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        obtenTablaBasica(ConstantesReportes.FILA_NOMBRE,Utilidades.validaString(persona.getNombre()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_APELLIDO_PATERNO,Utilidades.validaString(persona.getApellidoPaterno()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_APELLIDO_MATERNO,Utilidades.validaString(persona.getApellidoMaterno()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_GENERO,this.genero(Utilidades.validaString(persona.getGenero())),lista);
        obtenTablaBasica(ConstantesReportes.FILA_FECHA_NACIMIENTO,getFechaParseada(persona.getFechaNacimiento()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_CURP,Utilidades.validaString(persona.getCurp()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_RFC,Utilidades.validaString(persona.getRfc()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_NSS,Utilidades.validaString(persona.getNss()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_EMAIL_CORPORATIVO,Utilidades.validaString(persona.getEmailCorporativo()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_EMAIL_PERSONAL,Utilidades.validaString(persona.getContactoInicialEmailPersonal()),lista);
        return lista;
    }

    private List<TablaBasicaDto> obtenDomicilio(Integer idPersona) throws ServiceException {
        List<DomicilioConsulta> listaDomicilio = domicilioRepository.consultaDomicilioEmleado(idPersona);
        List<TablaBasicaDto> lista = new ArrayList<>();
        if(listaDomicilio.isEmpty()){
            obtenTablaBasica(ConstantesReportes.FILA_CODIGO_POSTAL,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_ENTIDAD_FEDERATIVA,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_MUNICIPIO,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_COLONIA,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_CALLE,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_NUMERO_EXTERIOR,"",lista);
            obtenTablaBasica(ConstantesReportes.FILA_NUMERO_INTERIOR,"",lista);
        }else{
            obtenTablaBasica(ConstantesReportes.FILA_CODIGO_POSTAL,Utilidades.validaString(listaDomicilio.get(0).getCodigoPostal()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_ENTIDAD_FEDERATIVA,Utilidades.validaString(listaDomicilio.get(0).getEntidadFederativa()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_MUNICIPIO,Utilidades.validaString(listaDomicilio.get(0).getMunicipio()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_COLONIA,Utilidades.validaString(listaDomicilio.get(0).getColonia()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_CALLE,Utilidades.validaString(listaDomicilio.get(0).getCalle()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_NUMERO_EXTERIOR,Utilidades.validaString(listaDomicilio.get(0).getNumeroExterior()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_NUMERO_INTERIOR,Utilidades.validaString(listaDomicilio.get(0).getNumeroInterior()),lista);
        }
        return lista;
    }

    private List<TablaBasicaDto> obtenDetallePuesto(NcoContratoColaborador contratoColaborador) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        obtenTablaBasica(ConstantesReportes.FILA_AREA,Utilidades.validaString(contratoColaborador.getAreaId().getDescripcion()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_PUESTO,Utilidades.validaString(contratoColaborador.getPuestoId().getDescripcion()),lista);
        if(contratoColaborador.getJefeInmediatoId() != null && contratoColaborador.getJefeInmediatoId().getPersonaId() != null){
            contratoColaborador.setJefeInmediatoId(ncoPersonaRepository.findByPersonaId(contratoColaborador.getJefeInmediatoId().getPersonaId()));
            obtenTablaBasica(ConstantesReportes.FILA_REPORTA,Utilidades.validaString(
                    contratoColaborador.getJefeInmediatoId().getNombre() + " " +
                            contratoColaborador.getJefeInmediatoId().getApellidoPaterno() + " "),lista);
        }else {
            obtenTablaBasica(ConstantesReportes.FILA_REPORTA,"",lista);
        }

        if (contratoColaborador.getSedeId() != null && contratoColaborador.getSedeId().getSedeId() != null ){
            NclSede sede = nclSedeRepository.findById(contratoColaborador.getSedeId().getSedeId()).orElse(new NclSede());
            obtenTablaBasica(ConstantesReportes.FILA_SEDE,Utilidades.validaString(sede.getDescripcion()),lista);
        }else {
            obtenTablaBasica(ConstantesReportes.FILA_SEDE,"",lista);
        }


        obtenTablaBasica(ConstantesReportes.FILA_ESTADO,Utilidades.validaString(contratoColaborador.getEstadoId().getEstado()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_POLITICAS,Utilidades.validaString(contratoColaborador.getPoliticaId().getNombre()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_NUMERO_EMPLEADO,Utilidades.validaString(contratoColaborador.getNumEmpleado()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_SINDICALIZADO,contratoColaborador.isEsSindicalizado() ? "SI" : "NO",lista);
        return lista;
    }

    private List<TablaBasicaDto> obtenCompensacion(NcoContratoColaborador contratoColaborador) throws ServiceException {
        List<TablaBasicaDto> lista = new ArrayList<>();
        try {
            Integer diferenciaAnio = getDiferenciaAnio(contratoColaborador.getFechaAntiguedad().toString().replace("-", "/"));
            BeneficiosPolitica beneficiosPolitica = politicaRepository.
                    obtenBeneficiosPolitica
                            (diferenciaAnio,contratoColaborador.getPoliticaId().getPoliticaId());
            obtenTablaBasica(ConstantesReportes.FILA_GRUPO_NOMINA, Utilidades.validaString(
                    contratoColaborador.getGrupoNominaId().getNombre()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_TIPO_COMPENSACION, Utilidades.validaString(
                    contratoColaborador.getTipoCompensacionId().getDescripcion()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_SUELDO_BASE,
                    Utilidades.formatoMoneda(contratoColaborador.getSbc()),lista);
            obtenTablaBasica(ConstantesReportes.FILA_SUELDO_DIARIO,
                    Utilidades.formatoMoneda(contratoColaborador.getSalarioDiario()),lista);

            Integer vacaciones = beneficiosPolitica.getDiasVacaciones() + (contratoColaborador.getDiasVacaciones()== null ? 0: contratoColaborador.getDiasVacaciones());

            obtenTablaBasica(ConstantesReportes.FILA_DIAS_VACACIONES,
                    vacaciones.toString(),lista);

            /** condicion para mostrar informacion solo a pago complemetario = true*/
            if (contratoColaborador.getGrupoNominaId().isPagoComplementario()) {
                obtenTablaBasica(ConstantesReportes.SUELDO_NETO_MENSUAL_PPP,
                        Utilidades.formatoMoneda(contratoColaborador.getPppSnm()),lista);
                obtenTablaBasica(ConstantesReportes.SALARIO_DIARIO_INTEGRADO,
                        Utilidades.formatoMoneda(contratoColaborador.getSbc()),lista);
                obtenTablaBasica(ConstantesReportes.PAGO_IMSS,
                        Utilidades.formatoMoneda(contratoColaborador.getSueldoNetoMensual()),lista);
                obtenTablaBasica(ConstantesReportes.PAGO_COMPLEMENTARIO,
                        Utilidades.formatoMoneda(contratoColaborador.getPppMontoComplementario()),lista);
                obtenTablaBasica(ConstantesReportes.SUELDO_BRUTO_MENSUAL,
                        Utilidades.formatoMoneda(contratoColaborador.getPppSalarioBaseMensual()),lista);
            } else {
                obtenTablaBasica(ConstantesReportes.FILA_SUELDO_MENSUAL,
                        Utilidades.formatoMoneda(contratoColaborador.getSueldoBrutoMensual()),lista);
                obtenTablaBasica(ConstantesReportes.FILA_SUELDO_NETO_MENSUAL,
                        Utilidades.formatoMoneda(contratoColaborador.getSueldoNetoMensual()),lista);
                obtenTablaBasica(ConstantesReportes.FILA_SUELDO_DIARIO_INTEGRADO,
                        Utilidades.formatoMoneda(contratoColaborador.getSbc()),lista);
            }
            return lista;
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenCompensacion " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    private Integer getDiferenciaAnio(String inicioFecha) {
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        fecha.format(LocalDateTime.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        ChronoLocalDate actual = ChronoLocalDate.from(formatter.parse(fecha.format(LocalDateTime.now())));
        ChronoLocalDate inicio = ChronoLocalDate.from(formatter.parse(inicioFecha));
        ChronoPeriod diferencia = ChronoPeriod.between(inicio,actual);
        Integer diferenciaAnio = Math.toIntExact(diferencia.get(YEARS));
        return diferenciaAnio == 0 ? 1 : diferenciaAnio;
    }

    private List<TablaBasicaDto> obtenInformacionContrato(NcoContratoColaborador contratoColaborador, NclCentrocCliente empresa) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        obtenTablaBasica(ConstantesReportes.FILA_NOMBRE_EMPRESA,Utilidades.validaString(empresa.getRazonSocial()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_TIPO_REGIMEN_CONTRATACION,
                Utilidades.validaString(contratoColaborador.getTipoRegimenContratacionId().getDescripcion()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_TIPO_CONTRATO,
                Utilidades.validaString(contratoColaborador.getTipoContratoId().getDescripcion()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_FECHA_INICIO_CONTRATO,
                getFechaParseada(contratoColaborador.getFechaContrato()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_JORNADA,Utilidades.validaString(contratoColaborador.getJornadaId().getNombre()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_FECHA_FIN_CONTRATO,
                getFechaParseada(contratoColaborador.getFechaFin()),lista);
        /**obtenTablaBasica(ConstantesReportes.FILA_FECHA_ALTA_IMSS,Utilidades.fechaTexto(contratoColaborador.getFechaAltaImss())));*/
        obtenTablaBasica(ConstantesReportes.FILA_AREA_GEOGRAFICA,
                Utilidades.validaString(contratoColaborador.getAreaGeograficaId().getDescripcion()),lista);
        /**obtenTablaBasica(ConstantesReportes.FILA_FECHA_ALTA_IMSS,contratoColaborador.getFechaAltaImss() != null ? contratoColaborador.getFechaAltaImss().toString() : ""));*/
        return lista;
    }

    private String getFechaParseada(Date fecha){
        return fecha == null?"":new SimpleDateFormat("dd-MMM-yyyy").format(fecha).replace(".","");
    }

    private void obtenTablaBasica(String titulo, String descipcion, List<TablaBasicaDto> lista){
        if(!descipcion.isEmpty()){
            lista.add(new TablaBasicaDto(titulo,descipcion));
        }
    }

    private List<TablaBasicaDto> obtenInformacionContactoEmergencia(NcoPersona persona) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        CatParentesco parentesco = null;
        if (persona.getParentescoId() != null && persona.getParentescoId().getParentescoId() != null){
            parentesco = catParentescoRepository.findById(persona.getParentescoId().getParentescoId()).orElse(null);
        }
        
        obtenTablaBasica(ConstantesReportes.FILA_NOMBRE,Utilidades.validaString(persona.getContactoEmergenciaNombre()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_APELLIDO_PATERNO,Utilidades.validaString(persona.getContactoEmergenciaApellidoPaterno()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_APELLIDO_MATERNO,Utilidades.validaString(persona.getContactoEmergenciaApellidoMaterno()),lista);

        if(parentesco != null){
            obtenTablaBasica(ConstantesReportes.FILA_PARENTESCO,Utilidades.validaString(parentesco.getDescripcion()),lista);
        }else {
            obtenTablaBasica(ConstantesReportes.FILA_PARENTESCO,"",lista);
        }

        obtenTablaBasica(ConstantesReportes.FILA_EMAIL_PERSONAL,Utilidades.validaString(persona.getContactoEmergenciaEmail()),lista);
        obtenTablaBasica(ConstantesReportes.FILA_CEULAR,Utilidades.numeroTexto(persona.getContactoEmergenciaTelefono()),lista);
        return lista;
    }

    private List<TablaBasicaDto> obtenInformacionPreferencias() {
        List<TablaBasicaDto> lista = new ArrayList<>();
        obtenTablaBasica(ConstantesReportes.FILA_ALIMENTACION,"",lista);
        obtenTablaBasica(ConstantesReportes.FILA_ALERGIAS,"",lista);
        obtenTablaBasica(ConstantesReportes.FILA_COLOR,"",lista);
        obtenTablaBasica(ConstantesReportes.FILA_BEBIDA,"",lista);
        obtenTablaBasica(ConstantesReportes.FILA_CAFE,"",lista);
        obtenTablaBasica(ConstantesReportes.FILA_SALADO_DULCE,"",lista);
        return lista;
    }

    private List<TablaBasicaDto> obtenInformacionAdicional(NcoPersona persona) {
        List<TablaBasicaDto> lista = new ArrayList<>();
        if(persona.getNacionalidadId() != null && persona.getNacionalidadId().getNacionalidadId() != null){
            persona.setNacionalidadId(catNacionalidadRepository.findById(persona.getNacionalidadId().getNacionalidadId()).orElse(new CatNacionalidad()));
            obtenTablaBasica(ConstantesReportes.FILA_NACIONALIDAD,
                    Utilidades.validaString(persona.getNacionalidadId().getDescripcion()),lista);
        }else {
            obtenTablaBasica(ConstantesReportes.FILA_NACIONALIDAD,"",lista);
        }
        obtenTablaBasica(ConstantesReportes.FILA_ESTADO_CIVIL,
                Utilidades.estadoCivil(Utilidades.validaString(persona.getEstadoCivil())),lista);
        obtenTablaBasica(ConstantesReportes.FILA_CEULAR,Utilidades.bigIntegerLong(persona.getCelular()) != 0
                ? Utilidades.bigIntegerLong(persona.getCelular()).toString() : "",lista);
        obtenTablaBasica(ConstantesReportes.FILA_TIENE_HIJOS,persona.isTieneHijos() ? "SI" : "NO",lista);
        obtenTablaBasica(ConstantesReportes.FILA_NUMERO_HIJOS,Utilidades.numeroTexto(persona.getNumeroHijos()),lista);
        return lista;
    }

    private List<EmpleadoRecuentoDto>  obtenListaEmpleados(List<NcoContratoColaborador> listaContratoColaborador){
        List<EmpleadoRecuentoDto> listaEmpleados = new ArrayList<>();
        listaContratoColaborador.forEach(contratoColaborador -> {
            EmpleadoRecuentoDto empleadosDto = new EmpleadoRecuentoDto();
            empleadosDto.setId(contratoColaborador.getNumEmpleado());
            empleadosDto.setApellidoPaterno(contratoColaborador.getPersonaId().getApellidoPaterno());
            empleadosDto.setApellidoMaterno(contratoColaborador.getPersonaId().getApellidoMaterno());
            empleadosDto.setNombre(contratoColaborador.getPersonaId().getNombre());
            empleadosDto.setArea(contratoColaborador.getAreaId().getDescripcion());
            empleadosDto.setPuesto(contratoColaborador.getPuestoId().getDescripcion());
            listaEmpleados.add(empleadosDto);
        });
        return listaEmpleados;
    }

    private String genero(String genero) {
        if (genero != null) {
            if (genero.equalsIgnoreCase("M")) {
                return "Masculino";
            } else {
                return "Femenino";
            }
        } else {
            return "";
        }
    }

}
