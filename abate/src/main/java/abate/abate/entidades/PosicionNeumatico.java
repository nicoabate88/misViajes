//
package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PosicionNeumatico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private Integer posicion;
    @ManyToOne
    private Eje eje;
    @ManyToOne
    private Neumatico neumatico;
    private String estado;

    public PosicionNeumatico() {
    }

    public PosicionNeumatico(Long id, Long idOrg, Integer posicion, Eje eje, Neumatico neumatico, String estado) {
        this.id = id;
        this.idOrg = idOrg;
        this.posicion = posicion;
        this.eje = eje;
        this.neumatico = neumatico;
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

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Eje getEje() {
        return eje;
    }

    public void setEje(Eje eje) {
        this.eje = eje;
    }

    public Neumatico getNeumatico() {
        return neumatico;
    }

    public void setNeumatico(Neumatico neumatico) {
        this.neumatico = neumatico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
}
