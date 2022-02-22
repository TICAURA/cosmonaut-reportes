package mx.com.ga.cosmonaut.reportes.dto.respuesta.descargadispersion;

import com.google.gson.annotations.SerializedName;


public class EmpleadoADispersarLiquidacionImpl {

    @SerializedName("empleadoADispersarLiquidacion")
    private EmpleadoADispersarLiquidacion empleadoADispersarLiquidacion;

    public EmpleadoADispersarLiquidacion getEmpleadoADispersarLiquidacion() {
        return empleadoADispersarLiquidacion;
    }

    public void setEmpleadoADispersarLiquidacion(EmpleadoADispersarLiquidacion empleadoADispersarLiquidacion) {
        this.empleadoADispersarLiquidacion = empleadoADispersarLiquidacion;
    }
}
