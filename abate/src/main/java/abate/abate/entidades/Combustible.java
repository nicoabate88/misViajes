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
public class Combustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idCarga;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fechaCarga;
    private Double kmCarga;
    private Double kmAnterior;
    private Double kmRecorrido;
    private Double litro;
    private Double consumo;
    private Double consumoPromedio;
    private String completo;
    private String estado;
    @OneToOne
    private Camion camion;
    @OneToOne
    private Acoplado acoplado;
    @OneToOne
    private Imagen imagen;
    @OneToOne
    private Usuario chofer;
    @OneToOne
    private Usuario usuario;
    @OneToOne
    private Azul azul;

    public Combustible() {
    }

    public Combustible(Long id, Long idCarga, Long idOrg, Date fechaCarga, Double kmCarga, Double kmAnterior, Double kmRecorrido, Double litro, Double consumo, Double consumoPromedio, String completo, String estado, Camion camion, Acoplado acoplado, Imagen imagen, Usuario chofer, Usuario usuario, Azul azul) {
        this.id = id;
        this.idCarga = idCarga;
        this.idOrg = idOrg;
        this.fechaCarga = fechaCarga;
        this.kmCarga = kmCarga;
        this.kmAnterior = kmAnterior;
        this.kmRecorrido = kmRecorrido;
        this.litro = litro;
        this.consumo = consumo;
        this.consumoPromedio = consumoPromedio;
        this.completo = completo;
        this.estado = estado;
        this.camion = camion;
        this.acoplado = acoplado;
        this.imagen = imagen;
        this.chofer = chofer;
        this.usuario = usuario;
        this.azul = azul;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCarga() {
        return idCarga;
    }

    public void setIdCarga(Long idCarga) {
        this.idCarga = idCarga;
    }

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public Double getKmCarga() {
        return kmCarga;
    }

    public void setKmCarga(Double kmCarga) {
        this.kmCarga = kmCarga;
    }

    public Double getKmAnterior() {
        return kmAnterior;
    }

    public void setKmAnterior(Double kmAnterior) {
        this.kmAnterior = kmAnterior;
    }

    public Double getKmRecorrido() {
        return kmRecorrido;
    }

    public void setKmRecorrido(Double kmRecorrido) {
        this.kmRecorrido = kmRecorrido;
    }

    public Double getLitro() {
        return litro;
    }

    public void setLitro(Double litro) {
        this.litro = litro;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public Double getConsumoPromedio() {
        return consumoPromedio;
    }

    public void setConsumoPromedio(Double consumoPromedio) {
        this.consumoPromedio = consumoPromedio;
    }

    public String getCompleto() {
        return completo;
    }

    public void setCompleto(String completo) {
        this.completo = completo;
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

    public Acoplado getAcoplado() {
        return acoplado;
    }

    public void setAcoplado(Acoplado acoplado) {
        this.acoplado = acoplado;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
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

    public Azul getAzul() {
        return azul;
    }

    public void setAzul(Azul azul) {
        this.azul = azul;
    }

    

    
}
