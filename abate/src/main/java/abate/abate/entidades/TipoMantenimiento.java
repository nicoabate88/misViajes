
package abate.abate.entidades;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class TipoMantenimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String nombre;
    @OneToOne
    private Usuario usuario;
    @Enumerated(EnumType.STRING)
    private Clase clase;
    @ElementCollection(targetClass = AplicaA.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "tipo_mantenimiento_aplica_a", joinColumns = @JoinColumn(name = "tipo_mantenimiento_id"))
    @Enumerated(EnumType.STRING)
    private List<AplicaA> aplicaA;  

    public enum AplicaA {
        CAMION, ACOPLADO,
    }
    
        public enum Clase {
    PREVENTIVO, CORRECTIVO
    }

    public TipoMantenimiento() {
    }

    public TipoMantenimiento(Long id, Long idOrg, String nombre, Usuario usuario, Clase clase, List<AplicaA> aplicaA) {
        this.id = id;
        this.idOrg = idOrg;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clase = clase;
        this.aplicaA = aplicaA;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Clase getClase() {
        return clase;
    }

    public void setClase(Clase clase) {
        this.clase = clase;
    }

    public List<AplicaA> getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(List<AplicaA> aplicaA) {
        this.aplicaA = aplicaA;
    }
    
}
