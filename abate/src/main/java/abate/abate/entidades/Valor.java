
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
public class Valor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recibo_id")
    private Recibo recibo;
    @Enumerated(EnumType.STRING)
    private TipoValor tipo; // EFECTIVO, TRANSFERENCIA, CHEQUE, etc (opcional)
    private Double importe;
    private String observacion; // opcional
    
    public enum TipoValor {
    EFECTIVO,
    TRANSFERENCIA,
    ECHEQ,
    CHEQUE,
    TARJETA,
    RETENCION,
    OTRO
}

    public Valor() {
    }

    public Valor(Long id, Recibo recibo, TipoValor tipo, Double importe, String observacion) {
        this.id = id;
        this.recibo = recibo;
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

    public Recibo getRecibo() {
        return recibo;
    }

    public void setRecibo(Recibo recibo) {
        this.recibo = recibo;
    }

    public TipoValor getTipo() {
        return tipo;
    }

    public void setTipo(TipoValor tipo) {
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
