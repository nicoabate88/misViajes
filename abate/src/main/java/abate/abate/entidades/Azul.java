package abate.abate.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Azul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private Double litro;
    private String estado;
    @OneToOne
    private Camion camion;
    @OneToOne
    private Usuario chofer;
    @OneToOne
    private Usuario usuario;

    public Azul() {
    }

    public Azul(Long id, Long idOrg, Date fecha, Double litro, String estado, Camion camion, Usuario chofer, Usuario usuario) {
        this.id = id;
        this.idOrg = idOrg;
        this.fecha = fecha;
        this.litro = litro;
        this.estado = estado;
        this.camion = camion;
        this.chofer = chofer;
        this.usuario = usuario;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getLitro() {
        return litro;
    }

    public void setLitro(Double litro) {
        this.litro = litro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
