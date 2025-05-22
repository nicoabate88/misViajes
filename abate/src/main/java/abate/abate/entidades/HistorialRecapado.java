
package abate.abate.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class HistorialRecapado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fechaEnvio;
    @Temporal(TemporalType.DATE)
    private Date fechaReingreso;
    private Integer kmAlRecapar;
    private Integer kmEstimado;
    private Integer kmFinalRecapado;
    private Integer kmRecapado;
    @OneToOne
    private NeumaticoProveedor proveedor;
    private String observacion;
    @ManyToOne
    private Neumatico neumatico;
    private String estado;
    @OneToOne
    private Usuario usuario;

    public HistorialRecapado() {
    }

    public HistorialRecapado(Long id, Long idOrg, Date fechaEnvio, Date fechaReingreso, Integer kmAlRecapar, Integer kmEstimado, Integer kmFinalRecapado, Integer kmRecapado, NeumaticoProveedor proveedor, String observacion, Neumatico neumatico, String estado, Usuario usuario) {
        this.id = id;
        this.idOrg = idOrg;
        this.fechaEnvio = fechaEnvio;
        this.fechaReingreso = fechaReingreso;
        this.kmAlRecapar = kmAlRecapar;
        this.kmEstimado = kmEstimado;
        this.kmFinalRecapado = kmFinalRecapado;
        this.kmRecapado = kmRecapado;
        this.proveedor = proveedor;
        this.observacion = observacion;
        this.neumatico = neumatico;
        this.estado = estado;
        this.usuario = usuario;
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

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFechaReingreso() {
        return fechaReingreso;
    }

    public void setFechaReingreso(Date fechaReingreso) {
        this.fechaReingreso = fechaReingreso;
    }

    public Integer getKmAlRecapar() {
        return kmAlRecapar;
    }

    public void setKmAlRecapar(Integer kmAlRecapar) {
        this.kmAlRecapar = kmAlRecapar;
    }

    public Integer getKmEstimado() {
        return kmEstimado;
    }

    public void setKmEstimado(Integer kmEstimado) {
        this.kmEstimado = kmEstimado;
    }

    public Integer getKmFinalRecapado() {
        return kmFinalRecapado;
    }

    public void setKmFinalRecapado(Integer kmFinalRecapado) {
        this.kmFinalRecapado = kmFinalRecapado;
    }

    public Integer getKmRecapado() {
        return kmRecapado;
    }

    public void setKmRecapado(Integer kmRecapado) {
        this.kmRecapado = kmRecapado;
    }

    public NeumaticoProveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(NeumaticoProveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Neumatico getNeumatico() {
        return neumatico;
    }

    public void setNeumatico(Neumatico neumatico) {
        this.neumatico = neumatico;
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
    
    


    
    
    
}
