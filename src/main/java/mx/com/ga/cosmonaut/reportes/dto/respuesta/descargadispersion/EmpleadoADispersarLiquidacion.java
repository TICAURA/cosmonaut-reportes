package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargadispersion;

import com.google.gson.annotations.SerializedName;


public class EmpleadoADispersarLiquidacion {

    @SerializedName("idEmpleado")
    private String idEmpleado;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("apellidos")
    private String apellidos;
    @SerializedName("rfc")
    private String rfc;
    @SerializedName("curp")
    private String curp;
    @SerializedName("moneda")
    private String moneda;
    @SerializedName("montoAPagar")
    private String montoAPagar;
    @SerializedName("banco")
    private String banco;
    @SerializedName("numeroCuenta")
    private String numeroCuenta;
    @SerializedName("nomina")
    private String nomina;

    public String getProperty(String info) {
        switch(info)
        {
            case "0": return idEmpleado == null ? "": getIdEmpleado();
            case "1": return nombre == null ? "" : getNombre();
            case "2": return apellidos == null ? "" : getApellidos();
            case "3": return rfc == null ? "" : getRfc();
            case "4": return curp == null ? "" : getCurp();
            case "5": return moneda == null ? "" : getMoneda();
            case "6": return montoAPagar == null ? "" : getMontoAPagar();
            case "7": return banco == null ? "" : getBanco();
            case "8": return numeroCuenta == null ? "" : getNumeroCuenta();
            case "9": return nomina == null ? "" : getNomina();
            default: return "";
        }
    }


    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getMontoAPagar() {
        return montoAPagar;
    }

    public void setMontoAPagar(String montoAPagar) {
        this.montoAPagar = montoAPagar;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getNomina() {
        return nomina;
    }

    public void setNomina(String nomina) {
        this.nomina = nomina;
    }
}
