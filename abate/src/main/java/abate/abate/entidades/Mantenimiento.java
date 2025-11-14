
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
public class Mantenimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @ManyToOne
    @JoinColumn(name = "tipo_mantenimiento_id", nullable = false)
    private TipoMantenimiento tipoMantenimiento;
    @Enumerated(EnumType.STRING)
    private TipoMantenimiento.AplicaA aplicaA;
    private String observacion;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Temporal(TemporalType.DATE)
    private Date fechaActualizado;
    private Integer km;
    private Integer kmProximo;
    private Integer kmAlarma;
    private Integer kmActual;
    private Integer kmVigencia;
    private String estado;
    @OneToOne
    private Usuario usuario;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;

    public Mantenimiento() {
    }

    public Mantenimiento(Long id, Long idOrg, TipoMantenimiento tipoMantenimiento, TipoMantenimiento.AplicaA aplicaA, String observacion, Date fecha, Date fechaActualizado, Integer km, Integer kmProximo, Integer kmAlarma, Integer kmActual, Integer kmVigencia, String estado, Usuario usuario, Camion camion, Acoplado acoplado) {
        this.id = id;
        this.idOrg = idOrg;
        this.tipoMantenimiento = tipoMantenimiento;
        this.aplicaA = aplicaA;
        this.observacion = observacion;
        this.fecha = fecha;
        this.fechaActualizado = fechaActualizado;
        this.km = km;
        this.kmProximo = kmProximo;
        this.kmAlarma = kmAlarma;
        this.kmActual = kmActual;
        this.kmVigencia = kmVigencia;
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

    public TipoMantenimiento getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimiento tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public TipoMantenimiento.AplicaA getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(TipoMantenimiento.AplicaA aplicaA) {
        this.aplicaA = aplicaA;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaActualizado() {
        return fechaActualizado;
    }

    public void setFechaActualizado(Date fechaActualizado) {
        this.fechaActualizado = fechaActualizado;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Integer getKmProximo() {
        return kmProximo;
    }

    public void setKmProximo(Integer kmProximo) {
        this.kmProximo = kmProximo;
    }

    public Integer getKmAlarma() {
        return kmAlarma;
    }

    public void setKmAlarma(Integer kmAlarma) {
        this.kmAlarma = kmAlarma;
    }

    public Integer getKmActual() {
        return kmActual;
    }

    public void setKmActual(Integer kmActual) {
        this.kmActual = kmActual;
    }

    public Integer getKmVigencia() {
        return kmVigencia;
    }

    public void setKmVigencia(Integer kmVigencia) {
        this.kmVigencia = kmVigencia;
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
