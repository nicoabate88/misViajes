package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private String marca;
    private String modelo;
    private String dominio;
    private String azul;

    public Camion() {
    }

    public Camion(Long id, Long idOrg, String marca, String modelo, String dominio, String azul) {
        this.id = id;
        this.idOrg = idOrg;
        this.marca = marca;
        this.modelo = modelo;
        this.dominio = dominio;
        this.azul = azul;
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

    public String getAzul() {
        return azul;
    }

    public void setAzul(String azul) {
        this.azul = azul;
    }

}
