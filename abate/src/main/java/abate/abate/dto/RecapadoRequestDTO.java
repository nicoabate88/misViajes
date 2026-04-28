
package abate.abate.dto;

import java.util.List;

public class RecapadoRequestDTO {
    
    private String fecha;
    private Long idProveedor;
    private String observacion;
    private List<RecapadoItemDTO> recapados;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<RecapadoItemDTO> getRecapados() {
        return recapados;
    }

    public void setRecapados(List<RecapadoItemDTO> recapados) {
        this.recapados = recapados;
    }
    
}
