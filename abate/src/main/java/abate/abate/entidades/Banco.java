
package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Banco {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrg;

    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private EstadoBanco estado;
        
    public enum EstadoBanco {
    HABILITADA,
    INHABILITADA
    }

    public Banco() {
    }

    public Banco(Long id, Long idOrg, String nombre, EstadoBanco estado) {
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
    
    public EstadoBanco getEstado() {
        return estado;
    }

    public void setEstado(EstadoBanco estado) {
        this.estado = estado;
    }
    
    
    
    
}
