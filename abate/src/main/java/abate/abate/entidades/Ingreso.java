package abate.abate.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idIngreso;
    private Long idOrg;
    @OneToOne
    private Usuario chofer;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private Double importe;
    private String observacion;
    @OneToOne
    private Usuario usuario;
    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorI> valores = new ArrayList<>();
    
        // Helpers para mantener la relación consistente
    public void addValor(ValorI v) {
        valores.add(v);
        v.setIngreso(this);
    }

    public void removeValor(ValorI v) {
        valores.remove(v);
        v.setIngreso(null);
    }


    public Ingreso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(Long idIngreso) {
        this.idIngreso = idIngreso;
    }

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ValorI> getValores() {
        return valores;
    }

    public void setValores(List<ValorI> valores) {
        this.valores = valores;
    }
    
    

}
