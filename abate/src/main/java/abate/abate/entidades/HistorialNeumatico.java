
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
public class HistorialNeumatico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fechaColocacion;
    private Integer kmColocacion;
    @Temporal(TemporalType.DATE)
    private Date fechaRetiro;
    private Integer kmRetiro;
    private Integer kmRecorrido;
    @ManyToOne
    private Neumatico neumatico;
    @ManyToOne
    private PosicionNeumatico posicion; 
    @OneToOne
    private Usuario usuario;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;
    private String estado;

    public HistorialNeumatico() {
    }

    public HistorialNeumatico(Long id, Long idOrg, Date fechaColocacion, Integer kmColocacion, Date fechaRetiro, Integer kmRetiro, Integer kmRecorrido, Neumatico neumatico, PosicionNeumatico posicion, Usuario usuario, Camion camion, Acoplado acoplado, String estado) {
        this.id = id;
        this.idOrg = idOrg;
        this.fechaColocacion = fechaColocacion;
        this.kmColocacion = kmColocacion;
        this.fechaRetiro = fechaRetiro;
        this.kmRetiro = kmRetiro;
        this.kmRecorrido = kmRecorrido;
        this.neumatico = neumatico;
        this.posicion = posicion;
        this.usuario = usuario;
        this.camion = camion;
        this.acoplado = acoplado;
        this.estado = estado;
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

    public Date getFechaColocacion() {
        return fechaColocacion;
    }

    public void setFechaColocacion(Date fechaColocacion) {
        this.fechaColocacion = fechaColocacion;
    }

    public Integer getKmColocacion() {
        return kmColocacion;
    }

    public void setKmColocacion(Integer kmColocacion) {
        this.kmColocacion = kmColocacion;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public Integer getKmRetiro() {
        return kmRetiro;
    }

    public void setKmRetiro(Integer kmRetiro) {
        this.kmRetiro = kmRetiro;
    }

    public Integer getKmRecorrido() {
        return kmRecorrido;
    }

    public void setKmRecorrido(Integer kmRecorrido) {
        this.kmRecorrido = kmRecorrido;
    }

    public Neumatico getNeumatico() {
        return neumatico;
    }

    public void setNeumatico(Neumatico neumatico) {
        this.neumatico = neumatico;
    }

    public PosicionNeumatico getPosicion() {
        return posicion;
    }

    public void setPosicion(PosicionNeumatico posicion) {
        this.posicion = posicion;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
}
