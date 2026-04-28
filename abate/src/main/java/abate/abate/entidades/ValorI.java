
package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ValorI {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingreso_id")
    private Ingreso ingreso;
    @Enumerated(EnumType.STRING)
    private TipoValorI tipo; // EFECTIVO, TRANSFERENCIA, CHEQUE, etc (opcional)
    private Double importe;
    private String observacion; // opcional

    public enum TipoValorI {
        EFECTIVO,
        TRANSFERENCIA,
        ECHEQ,
        CHEQUE,
        OTRO
    }

    public ValorI() {
    }

    public ValorI(Long id, Ingreso ingreso, TipoValorI tipo, Double importe, String observacion) {
        this.id = id;
        this.ingreso = ingreso;
        this.tipo = tipo;
        this.importe = importe;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ingreso getIngreso() {
        return ingreso;
    }

    public void setIngreso(Ingreso ingreso) {
        this.ingreso = ingreso;
    }

    public TipoValorI getTipo() {
        return tipo;
    }

    public void setTipo(TipoValorI tipo) {
        this.tipo = tipo;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
    
    
    
}
