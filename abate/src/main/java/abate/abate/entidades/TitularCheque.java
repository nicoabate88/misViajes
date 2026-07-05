
package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TitularCheque {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String nombre;
    @Enumerated(EnumType.STRING)
    private EstadoTitular estado;
        
    public enum EstadoTitular{
    HABILITADA,
    INHABILITADA
    }

    public TitularCheque() {
    }

    public TitularCheque(Long id, Long idOrg, String nombre, EstadoTitular estado) {
        this.id = id;
        this.idOrg = idOrg;
        this.nombre = nombre;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoTitular getEstado() {
        return estado;
    }

    public void setEstado(EstadoTitular estado) {
        this.estado = estado;
    }
    
    
    
}
