
package abate.abate.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Neumatico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idOrg;
    private Integer numero;
    @OneToOne
    private NeumaticoMarca marca;
    private String modelo;
    @OneToOne
    private NeumaticoProveedor proveedor;
    private Integer km;
    private Integer kmEstimado;
    private Integer kmUtil;
    private Integer kmIngreso;
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Temporal(TemporalType.DATE)
    private Date fechaEgreso;
    private String observacion;
    @OneToOne
    private Usuario usuario;
    private String estado;
    private String ubicacion;
    private String vehiculo;
    private String posicion;
    @OneToMany(mappedBy = "neumatico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialNeumatico> historial = new ArrayList<>();
    @OneToMany(mappedBy = "neumatico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialRecapado> recapados = new ArrayList<>();
    @ElementCollection(targetClass = AplicaA.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "neumatico_aplica_a", joinColumns = @JoinColumn(name = "neumatico_id"))
    @Enumerated(EnumType.STRING)
    private List<AplicaA> aplicaA;  

    public enum AplicaA {
        CAMION, ACOPLADO,
    }

    public Neumatico() {
    }

    public Neumatico(Long id, Long idOrg, Integer numero, NeumaticoMarca marca, String modelo, NeumaticoProveedor proveedor, Integer km, Integer kmEstimado, Integer kmUtil, Integer kmIngreso, Date fechaIngreso, Date fechaEgreso, String observacion, Usuario usuario, String estado, String ubicacion, String vehiculo, String posicion, List<AplicaA> aplicaA) {
        this.id = id;
        this.idOrg = idOrg;
        this.numero = numero;
        this.marca = marca;
        this.modelo = modelo;
        this.proveedor = proveedor;
        this.km = km;
        this.kmEstimado = kmEstimado;
        this.kmUtil = kmUtil;
        this.kmIngreso = kmIngreso;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.observacion = observacion;
        this.usuario = usuario;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.vehiculo = vehiculo;
        this.posicion = posicion;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public NeumaticoMarca getMarca() {
        return marca;
    }

    public void setMarca(NeumaticoMarca marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public NeumaticoProveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(NeumaticoProveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Integer getKmEstimado() {
        return kmEstimado;
    }

    public void setKmEstimado(Integer kmEstimado) {
        this.kmEstimado = kmEstimado;
    }

    public Integer getKmUtil() {
        return kmUtil;
    }

    public void setKmUtil(Integer kmUtil) {
        this.kmUtil = kmUtil;
    }

    public Integer getKmIngreso() {
        return kmIngreso;
    }

    public void setKmIngreso(Integer kmIngreso) {
        this.kmIngreso = kmIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaEgreso() {
        return fechaEgreso;
    }

    public void setFechaEgreso(Date fechaEgreso) {
        this.fechaEgreso = fechaEgreso;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public List<HistorialNeumatico> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialNeumatico> historial) {
        this.historial = historial;
    }

    public List<HistorialRecapado> getRecapados() {
        return recapados;
    }

    public void setRecapados(List<HistorialRecapado> recapados) {
        this.recapados = recapados;
    }

    public List<AplicaA> getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(List<AplicaA> aplicaA) {
        this.aplicaA = aplicaA;
    }

    
    
}
