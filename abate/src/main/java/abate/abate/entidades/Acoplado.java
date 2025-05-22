
package abate.abate.entidades;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Acoplado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String marca;
    private String modelo;
    private String dominio;
    private String estado;
    @OneToOne
    private Usuario usuario;
    @OneToMany(mappedBy = "acoplado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Eje> ejes = new ArrayList<>();
    private Integer cantidadAuxilio;

    public Acoplado() {
    }

    public Acoplado(Long id, Long idOrg, String marca, String modelo, String dominio, String estado, Usuario usuario, Integer cantidadAuxilio) {
        this.id = id;
        this.idOrg = idOrg;
        this.marca = marca;
        this.modelo = modelo;
        this.dominio = dominio;
        this.estado = estado;
        this.usuario = usuario;
        this.cantidadAuxilio = cantidadAuxilio;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Eje> getEjes() {
        return ejes;
    }

    public void setEjes(List<Eje> ejes) {
        this.ejes = ejes;
    }

    public Integer getCantidadAuxilio() {
        return cantidadAuxilio;
    }

    public void setCantidadAuxilio(Integer cantidadAuxilio) {
        this.cantidadAuxilio = cantidadAuxilio;
    }

    

    
    
    
}
