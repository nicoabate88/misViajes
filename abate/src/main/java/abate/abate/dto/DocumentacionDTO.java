
package abate.abate.dto;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class DocumentacionDTO {
    
    private Long id;
    private Long tipoDocumentacion; 
    private String aplicaA;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    private String observacion;
    private String estado;
    private Long camion;
    private Long acoplado;
    private Long chofer;

    public DocumentacionDTO() {
    }

    public DocumentacionDTO(Long id, Long tipoDocumentacion, String aplicaA, Date fechaAlta, Date fechaVencimiento, String observacion, String estado, Long camion, Long acoplado, Long chofer) {
        this.id = id;
        this.tipoDocumentacion = tipoDocumentacion;
        this.aplicaA = aplicaA;
        this.fechaAlta = fechaAlta;
        this.fechaVencimiento = fechaVencimiento;
        this.observacion = observacion;
        this.estado = estado;
        this.camion = camion;
        this.acoplado = acoplado;
        this.chofer = chofer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoDocumentacion() {
        return tipoDocumentacion;
    }

    public void setTipoDocumentacion(Long tipoDocumentacion) {
        this.tipoDocumentacion = tipoDocumentacion;
    }

    public String getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(String aplicaA) {
        this.aplicaA = aplicaA;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getCamion() {
        return camion;
    }

    public void setCamion(Long camion) {
        this.camion = camion;
    }

    public Long getAcoplado() {
        return acoplado;
    }

    public void setAcoplado(Long acoplado) {
        this.acoplado = acoplado;
    }

    public Long getChofer() {
        return chofer;
    }

    public void setChofer(Long chofer) {
        this.chofer = chofer;
    }

    
   
}
