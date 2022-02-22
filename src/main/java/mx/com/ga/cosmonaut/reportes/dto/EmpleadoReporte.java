package mx.com.ga.cosmonaut.reportes.dto;

import lombok.Data;

@Data
public class EmpleadoReporte {
    private String razonSocial;
    private String numEmpleado;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String fechaNacimiento;
    private String genero;
    private String rfc;
    private String curp;
    private String seguroSocial;
    private String registroPatronal;
    private String estadoCivil;
    private String ibaNumHijos;
    private String domicilioEmpleado;
    private String ciTelefono;
    private String celular;
    private String correoEmpresarial;
    private String correoPersonal;
    private String descripcionArea;
    private String descripcionPuesto;
    private String nombreJefe;
    private String fechaInicio;
    private String fechaFin;
    private String deFechaAntiguedad;
    private String baUltimoDia;
    private String esActivo;
    private String nombreRegimenContratacion;
    private String nombreTipoContrato;
    private String nombreGrupoNomina;
    private String nombrePolitica;
    private String nombreJornada;
    private String esSindicalizado;
    private String sueldoBrutoMensual;
    private String salarioDiario;
    private String salarioDiarioIntegrado;
    private String sbc;
    private String sueldoNetoMensual;
    private String nombreCompensacion;
    private String estadoEmpresa;
    private String areaGeografica;
    private String nombreMetodoPago;
    private String banco;
    private String numCuenta;
    private String emCuentaClabeStp;
    private String cuentaBancoId;
    private String nombreContacto;
    private String ceParentescoId;
    private String emailContacto;

    public String getProperty(String dato) {
        switch (dato) {
            case "0": return razonSocial == null ? "" : getRazonSocial();
            case "1": return numEmpleado == null ? "" : getNumEmpleado();
            case "2": return nombre == null ? "" : getNombre();
            case "3": return primerApellido == null ? "" : getPrimerApellido();
            case "4": return segundoApellido == null ? "" : getSegundoApellido();
            case "5": return fechaNacimiento == null ? "" : getFechaNacimiento();
            case "6": return genero == null ? "" : getGenero();
            case "7": return rfc == null ? "" : getRfc();
            case "8": return curp == null ? "" : getCurp();
            case "9": return seguroSocial == null ? "" : getSeguroSocial();
            case "10": return registroPatronal == null ? "" : getRegistroPatronal();
            case "11": return estadoCivil == null ? "" : getEstadoCivil();
            case "12": return ibaNumHijos == null ? "" : getIbaNumHijos();
            case "13": return domicilioEmpleado == null ? "" : getDomicilioEmpleado();
            case "14": return ciTelefono == null ? "" : getCiTelefono();
            case "15": return celular == null ? "" : getCelular();
            case "16": return correoEmpresarial == null ? "" : getCorreoEmpresarial();
            case "17": return correoPersonal == null ? "" : getCorreoPersonal();
            case "18": return descripcionArea == null ? "" : getDescripcionArea();
            case "19": return descripcionPuesto == null ? "" : getDescripcionPuesto();
            case "20": return nombreJefe == null ? "" : getNombreJefe();
            case "21": return fechaInicio == null ? "" : getFechaInicio();
            case "22": return fechaFin == null ? "" : getFechaFin();
            case "23": return deFechaAntiguedad == null ? "" : getDeFechaAntiguedad();
            case "24": return baUltimoDia == null ? "" : getBaUltimoDia();
            case "25": return esActivo == null ? "" : getEsActivo();
            case "26": return nombreRegimenContratacion == null ? "" : getNombreRegimenContratacion();
            case "27": return nombreTipoContrato == null ? "" : getNombreTipoContrato();
            case "28": return nombreGrupoNomina == null ? "" : getNombreGrupoNomina();
            case "29": return nombrePolitica == null ? "" : getNombrePolitica();
            case "30": return nombreJornada == null ? "" : getNombreJornada();
            case "31": return esSindicalizado == null ? "" : getEsSindicalizado();
            case "32": return sueldoBrutoMensual == null ? "" : getSueldoBrutoMensual();
            case "33": return salarioDiario == null ? "" : getSalarioDiario();
            case "34": return salarioDiarioIntegrado == null ? "" : getSalarioDiarioIntegrado();
            case "35": return sbc == null ? "" : getSbc();
            case "36": return sueldoNetoMensual == null ? "" : getSueldoNetoMensual();
            case "37": return nombreCompensacion == null ? "" : getNombreCompensacion();
            case "38": return estadoEmpresa == null ? "" : getEstadoEmpresa();
            case "39": return areaGeografica == null ? "" : getAreaGeografica();
            case "40": return nombreMetodoPago == null ? "" : getNombreMetodoPago();
            case "41": return banco == null ? "" : getBanco();
            case "42": return numCuenta == null ? "" : getNumCuenta();
            case "43": return emCuentaClabeStp == null ? "" : getEmCuentaClabeStp();
            case "44": return cuentaBancoId == null ? "" : getCuentaBancoId();
            case "45": return nombreContacto == null ? "" : getNombreContacto();
            case "46": return ceParentescoId == null ? "" : getCeParentescoId();
            case "47": return emailContacto == null ? "" : getEmailContacto();
            default: return "";
        }
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNumEmpleado() {
        return numEmpleado;
    }

    public void setNumEmpleado(String numEmpleado) {
        this.numEmpleado = numEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getSeguroSocial() {
        return seguroSocial;
    }

    public void setSeguroSocial(String seguroSocial) {
        this.seguroSocial = seguroSocial;
    }

    public String getRegistroPatronal() {
        return registroPatronal;
    }

    public void setRegistroPatronal(String registroPatronal) {
        this.registroPatronal = registroPatronal;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getIbaNumHijos() {
        return ibaNumHijos;
    }

    public void setIbaNumHijos(String ibaNumHijos) {
        this.ibaNumHijos = ibaNumHijos;
    }

    public String getDomicilioEmpleado() {
        return domicilioEmpleado;
    }

    public void setDomicilioEmpleado(String domicilioEmpleado) {
        this.domicilioEmpleado = domicilioEmpleado;
    }

    public String getCiTelefono() {
        return ciTelefono;
    }

    public void setCiTelefono(String ciTelefono) {
        this.ciTelefono = ciTelefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreoEmpresarial() {
        return correoEmpresarial;
    }

    public void setCorreoEmpresarial(String correoEmpresarial) {
        this.correoEmpresarial = correoEmpresarial;
    }

    public String getCorreoPersonal() {
        return correoPersonal;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public String getDescripcionArea() {
        return descripcionArea;
    }

    public void setDescripcionArea(String descripcionArea) {
        this.descripcionArea = descripcionArea;
    }

    public String getDescripcionPuesto() {
        return descripcionPuesto;
    }

    public void setDescripcionPuesto(String descripcionPuesto) {
        this.descripcionPuesto = descripcionPuesto;
    }

    public String getNombreJefe() {
        return nombreJefe;
    }

    public void setNombreJefe(String nombreJefe) {
        this.nombreJefe = nombreJefe;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDeFechaAntiguedad() {
        return deFechaAntiguedad;
    }

    public void setDeFechaAntiguedad(String deFechaAntiguedad) {
        this.deFechaAntiguedad = deFechaAntiguedad;
    }

    public String getBaUltimoDia() {
        return baUltimoDia;
    }

    public void setBaUltimoDia(String baUltimoDia) {
        this.baUltimoDia = baUltimoDia;
    }

    public String getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(String esActivo) {
        this.esActivo = esActivo;
    }

    public String getNombreRegimenContratacion() {
        return nombreRegimenContratacion;
    }

    public void setNombreRegimenContratacion(String nombreRegimenContratacion) {
        this.nombreRegimenContratacion = nombreRegimenContratacion;
    }

    public String getNombreTipoContrato() {
        return nombreTipoContrato;
    }

    public void setNombreTipoContrato(String nombreTipoContrato) {
        this.nombreTipoContrato = nombreTipoContrato;
    }

    public String getNombreGrupoNomina() {
        return nombreGrupoNomina;
    }

    public void setNombreGrupoNomina(String nombreGrupoNomina) {
        this.nombreGrupoNomina = nombreGrupoNomina;
    }

    public String getNombrePolitica() {
        return nombrePolitica;
    }

    public void setNombrePolitica(String nombrePolitica) {
        this.nombrePolitica = nombrePolitica;
    }

    public String getNombreJornada() {
        return nombreJornada;
    }

    public void setNombreJornada(String nombreJornada) {
        this.nombreJornada = nombreJornada;
    }

    public String getEsSindicalizado() {
        return esSindicalizado;
    }

    public void setEsSindicalizado(String esSindicalizado) {
        this.esSindicalizado = esSindicalizado;
    }

    public String getSueldoBrutoMensual() {
        return sueldoBrutoMensual;
    }

    public void setSueldoBrutoMensual(String sueldoBrutoMensual) {
        this.sueldoBrutoMensual = sueldoBrutoMensual;
    }

    public String getSalarioDiario() {
        return salarioDiario;
    }

    public void setSalarioDiario(String salarioDiario) {
        this.salarioDiario = salarioDiario;
    }

    public String getSalarioDiarioIntegrado() {
        return salarioDiarioIntegrado;
    }

    public void setSalarioDiarioIntegrado(String salarioDiarioIntegrado) {
        this.salarioDiarioIntegrado = salarioDiarioIntegrado;
    }

    public String getSbc() {
        return sbc;
    }

    public void setSbc(String sbc) {
        this.sbc = sbc;
    }

    public String getSueldoNetoMensual() {
        return sueldoNetoMensual;
    }

    public void setSueldoNetoMensual(String sueldoNetoMensual) {
        this.sueldoNetoMensual = sueldoNetoMensual;
    }

    public String getNombreCompensacion() {
        return nombreCompensacion;
    }

    public void setNombreCompensacion(String nombreCompensacion) {
        this.nombreCompensacion = nombreCompensacion;
    }

    public String getEstadoEmpresa() {
        return estadoEmpresa;
    }

    public void setEstadoEmpresa(String estadoEmpresa) {
        this.estadoEmpresa = estadoEmpresa;
    }

    public String getAreaGeografica() {
        return areaGeografica;
    }

    public void setAreaGeografica(String areaGeografica) {
        this.areaGeografica = areaGeografica;
    }

    public String getNombreMetodoPago() {
        return nombreMetodoPago;
    }

    public void setNombreMetodoPago(String nombreMetodoPago) {
        this.nombreMetodoPago = nombreMetodoPago;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }

    public String getEmCuentaClabeStp() {
        return emCuentaClabeStp;
    }

    public void setEmCuentaClabeStp(String emCuentaClabeStp) {
        this.emCuentaClabeStp = emCuentaClabeStp;
    }

    public String getCuentaBancoId() {
        return cuentaBancoId;
    }

    public void setCuentaBancoId(String cuentaBancoId) {
        this.cuentaBancoId = cuentaBancoId;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getCeParentescoId() {
        return ceParentescoId;
    }

    public void setCeParentescoId(String ceParentescoId) {
        this.ceParentescoId = ceParentescoId;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }
}
