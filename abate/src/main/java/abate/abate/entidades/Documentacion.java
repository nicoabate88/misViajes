
package abate.abate.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Documentacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @ManyToOne
    @JoinColumn(name = "tipo_documentacion_id", nullable = false)
    private TipoDocumentacion tipoDocumentacion;
    @Enumerated(EnumType.STRING)
    private TipoDocumentacion.AplicaA aplicaA;
    private String observacion;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    @OneToOne
    private Imagen imagen; 
    private Integer diasVigencia;
    private String estado;
    @OneToOne
    private Usuario usuario;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;
    @ManyToOne
    private Usuario chofer;

    public Documentacion() {
    }

    public Documentacion(Long id, Long idOrg, TipoDocumentacion tipoDocumentacion, TipoDocumentacion.AplicaA aplicaA, String observacion, Date fechaAlta, Date fechaVencimiento, Imagen imagen, Integer diasVigencia, String estado, Usuario usuario, Camion camion, Acoplado acoplado, Usuario chofer) {
        this.id = id;
        this.idOrg = idOrg;
        this.tipoDocumentacion = tipoDocumentacion;
        this.aplicaA = aplicaA;
        this.observacion = observacion;
        this.fechaAlta = fechaAlta;
        this.fechaVencimiento = fechaVencimiento;
        this.imagen = imagen;
        this.diasVigencia = diasVigencia;
        this.estado = estado;
        this.usuario = usuario;
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

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public TipoDocumentacion getTipoDocumentacion() {
        return tipoDocumentacion;
    }

    public void setTipoDocumentacion(TipoDocumentacion tipoDocumentacion) {
        this.tipoDocumentacion = tipoDocumentacion;
    }

    public TipoDocumentacion.AplicaA getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(TipoDocumentacion.AplicaA aplicaA) {
        this.aplicaA = aplicaA;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public Integer getDiasVigencia() {
        return diasVigencia;
    }

    public void setDiasVigencia(Integer diasVigencia) {
        this.diasVigencia = diasVigencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Acoplado getAcoplado() {
        return acoplado;
    }

    public void setAcoplado(Acoplado acoplado) {
        this.acoplado = acoplado;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }
    
    
    
}
