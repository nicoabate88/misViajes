
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
public class AuxilioNeumatico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @ManyToOne
    private Neumatico neumatico;
    @Temporal(TemporalType.DATE)
    private Date fechaColocacion;
    @Temporal(TemporalType.DATE)
    private Date fechaRetiro;
    private Integer posicion;
    private String estado;
    @OneToOne
    private Usuario usuario;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;

    public AuxilioNeumatico() {
    }

    public AuxilioNeumatico(Long id, Long idOrg, Neumatico neumatico, Date fechaColocacion, Date fechaRetiro, Integer posicion, String estado, Usuario usuario, Camion camion, Acoplado acoplado) {
        this.id = id;
        this.idOrg = idOrg;
        this.neumatico = neumatico;
        this.fechaColocacion = fechaColocacion;
        this.fechaRetiro = fechaRetiro;
        this.posicion = posicion;
        this.estado = estado;
        this.usuario = usuario;
        this.camion = camion;
        this.acoplado = acoplado;
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

    public Neumatico getNeumatico() {
        return neumatico;
    }

    public void setNeumatico(Neumatico neumatico) {
        this.neumatico = neumatico;
    }

    public Date getFechaColocacion() {
        return fechaColocacion;
    }

    public void setFechaColocacion(Date fechaColocacion) {
        this.fechaColocacion = fechaColocacion;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
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
    
    
    
}
