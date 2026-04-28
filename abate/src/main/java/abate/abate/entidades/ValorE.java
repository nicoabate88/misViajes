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
public class ValorE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;
    @Enumerated(EnumType.STRING)
    private TipoValorE tipo; // EFECTIVO, TRANSFERENCIA, CHEQUE, etc (opcional)
    private Double importe;
    private String observacion; // opcional

    public enum TipoValorE {
        EFECTIVO,
        TRANSFERENCIA,
        ECHEQ,
        CHEQUE,
        OTRO
    }

    public ValorE() {
    }

    public ValorE(Long id, Entrega entrega, TipoValorE tipo, Double importe, String observacion) {
        this.id = id;
        this.entrega = entrega;
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

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public TipoValorE getTipo() {
        return tipo;
    }

    public void setTipo(TipoValorE tipo) {
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
