
package abate.abate.entidades;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CuentaBancaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrg;

    private String banco;

    private String numeroCuenta;
    
    private BigDecimal limiteOperativo;

    private BigDecimal saldoDisponible;

    @Enumerated(EnumType.STRING)
    private EstadoCuentaBancaria estado;

    public CuentaBancaria() {
        this.saldoDisponible = BigDecimal.ZERO;
        this.estado = EstadoCuentaBancaria.HABILITADA;
    }
    
    public enum EstadoCuentaBancaria {
    HABILITADA,
    INHABILITADA
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

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getLimiteOperativo() {
        return limiteOperativo;
    }

    public void setLimiteOperativo(BigDecimal limiteOperativo) {
        this.limiteOperativo = limiteOperativo;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public EstadoCuentaBancaria getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuentaBancaria estado) {
        this.estado = estado;
    }
    
    
    
}
