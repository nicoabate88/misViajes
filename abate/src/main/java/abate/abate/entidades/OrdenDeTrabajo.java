package abate.abate.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class OrdenDeTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrden;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    @Temporal(TemporalType.DATE)
    private Date fechaCierre;
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    private Double duracion;
    private String lugar;
    private String observacion;
    @OneToOne
    private Usuario usuario;
    private String responsable;
    private String checkTaller;
    private String checkMetalurgica;
    private String checkAveria;
    private String checkNogoya;
    @ManyToOne
    private Proveedor proveedor;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;
    @ManyToOne
    private Usuario chofer;
    @OneToMany(mappedBy = "ordenDeTrabajo", cascade = CascadeType.ALL)
    private List<Mantenimiento> mantenimientos = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Estado {
        ABIERTO, EN_PROCESO, CERRADO, CANCELADO
    }
    
    public OrdenDeTrabajo() {
    }

    public OrdenDeTrabajo(Long id, Long idOrden, Long idOrg, Date fechaAlta, Date fechaCierre, Date fechaInicio, Date fechaFin, Double duracion, String lugar, String observacion, Usuario usuario, String responsable, String checkTaller, String checkMetalurgica, String checkAveria, String checkNogoya, Proveedor proveedor, Camion camion, Acoplado acoplado, Usuario chofer, Estado estado) {
        this.id = id;
        this.idOrden = idOrden;
        this.idOrg = idOrg;
        this.fechaAlta = fechaAlta;
        this.fechaCierre = fechaCierre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracion = duracion;
        this.lugar = lugar;
        this.observacion = observacion;
        this.usuario = usuario;
        this.responsable = responsable;
        this.checkTaller = checkTaller;
        this.checkMetalurgica = checkMetalurgica;
        this.checkAveria = checkAveria;
        this.checkNogoya = checkNogoya;
        this.proveedor = proveedor;
        this.camion = camion;
        this.acoplado = acoplado;
        this.chofer = chofer;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Double getDuracion() {
        return duracion;
    }

    public void setDuracion(Double duracion) {
        this.duracion = duracion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getCheckTaller() {
        return checkTaller;
    }

    public void setCheckTaller(String checkTaller) {
        this.checkTaller = checkTaller;
    }

    public String getCheckMetalurgica() {
        return checkMetalurgica;
    }

    public void setCheckMetalurgica(String checkMetalurgica) {
        this.checkMetalurgica = checkMetalurgica;
    }

    public String getCheckAveria() {
        return checkAveria;
    }

    public void setCheckAveria(String checkAveria) {
        this.checkAveria = checkAveria;
    }

    public String getCheckNogoya() {
        return checkNogoya;
    }

    public void setCheckNogoya(String checkNogoya) {
        this.checkNogoya = checkNogoya;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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

    public List<Mantenimiento> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<Mantenimiento> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    

    
    
    
    
}
