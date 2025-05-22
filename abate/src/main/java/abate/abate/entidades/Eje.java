
package abate.abate.entidades;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Eje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String nombre;
    private Integer numero;
    private Integer porcentaje;
    private Boolean elevable;
    private String estado;
    private Integer cantidadNeumatico;
    @OneToOne
    private Usuario usuario;
    @ManyToOne
    private Camion camion;
    @ManyToOne
    private Acoplado acoplado;
    @OneToMany(mappedBy = "eje", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PosicionNeumatico> posiciones = new ArrayList<>();

    public Eje() {
    }

    public Eje(Long id, Long idOrg, String nombre, Integer numero, Integer porcentaje, Boolean elevable, String estado, Integer cantidadNeumatico, Usuario usuario, Camion camion, Acoplado acoplado) {
        this.id = id;
        this.idOrg = idOrg;
        this.nombre = nombre;
        this.numero = numero;
        this.porcentaje = porcentaje;
        this.elevable = elevable;
        this.estado = estado;
        this.cantidadNeumatico = cantidadNeumatico;
        this.usuario = usuario;
        this.camion = camion;
        this.acoplado = acoplado;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Boolean getElevable() {
        return elevable;
    }

    public void setElevable(Boolean elevable) {
        this.elevable = elevable;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCantidadNeumatico() {
        return cantidadNeumatico;
    }

    public void setCantidadNeumatico(Integer cantidadNeumatico) {
        this.cantidadNeumatico = cantidadNeumatico;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Acoplado getAcoplado() {
        return acoplado;
    }

    public void setAcoplado(Acoplado acoplado) {
        this.acoplado = acoplado;
    }

    public List<PosicionNeumatico> getPosiciones() {
        return posiciones;
    }

    public void setPosiciones(List<PosicionNeumatico> posiciones) {
        this.posiciones = posiciones;
    }

    
}
