
package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String nombre;
    private Long cuit;
    private String estado;

    public Proveedor() {
    }

    public Proveedor(Long id, Long idOrg, String nombre, Long cuit, String estado) {
        this.id = id;
        this.idOrg = idOrg;
        this.nombre = nombre;
        this.cuit = cuit;
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

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
}
