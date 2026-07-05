
package abate.abate.entidades;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cheque {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrg;

    private String numeroCheque;

    @ManyToOne
    private Banco bancoEmisor;
    @ManyToOne
    private TitularCheque titularEmisor;
    private String titular;
    private BigDecimal importe;

    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Temporal(TemporalType.DATE)
    private Date fechaAcreditacion;

    @Temporal(TemporalType.DATE)
    private Date fechaVenta;

    private String observacion;

    @Enumerated(EnumType.STRING)
    private EstadoCheque estado;

    @ManyToOne
    private CuentaBancaria cuentaBancaria;

    public Cheque() {
        this.estado = EstadoCheque.EN_CARTERA;
    }
    
    public enum EstadoCheque {
    EN_CARTERA,
    VENDIDO,
    ACREDITADO,
    ANULADO
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

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public Banco getBancoEmisor() {
        return bancoEmisor;
    }

    public void setBancoEmisor(Banco bancoEmisor) {
        this.bancoEmisor = bancoEmisor;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaAcreditacion() {
        return fechaAcreditacion;
    }

    public void setFechaAcreditacion(Date fechaAcreditacion) {
        this.fechaAcreditacion = fechaAcreditacion;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public EstadoCheque getEstado() {
        return estado;
    }

    public void setEstado(EstadoCheque estado) {
        this.estado = estado;
    }

    public CuentaBancaria getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public TitularCheque getTitularEmisor() {
        return titularEmisor;
    }

    public void setTitularEmisor(TitularCheque titularEmisor) {
        this.titularEmisor = titularEmisor;
    }
    
}
