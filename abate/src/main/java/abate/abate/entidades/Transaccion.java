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
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Cliente cliente;
    @OneToOne
    private Usuario chofer;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String concepto;
    private String observacion;
    private Double importe;
    private Double saldoAcumulado;
    @OneToOne
    private Flete flete;
    @OneToOne
    private Recibo recibo;
    @OneToOne
    private Entrega entrega;
    @OneToOne
    private Gasto gasto;
    @OneToOne
    private Ingreso ingreso;

    public Transaccion() {
    }

    public Transaccion(Long id, Cliente cliente, Usuario chofer, Date fecha, String concepto, String observacion, Double importe, Double saldoAcumulado, Flete flete, Recibo recibo, Entrega entrega, Gasto gasto, Ingreso ingreso) {
        this.id = id;
        this.cliente = cliente;
        this.chofer = chofer;
        this.fecha = fecha;
        this.concepto = concepto;
        this.observacion = observacion;
        this.importe = importe;
        this.saldoAcumulado = saldoAcumulado;
        this.flete = flete;
        this.recibo = recibo;
        this.entrega = entrega;
        this.gasto = gasto;
        this.ingreso = ingreso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Double getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(Double saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public Flete getFlete() {
        return flete;
    }

    public void setFlete(Flete flete) {
        this.flete = flete;
    }

    public Recibo getRecibo() {
        return recibo;
    }

    public void setRecibo(Recibo recibo) {
        this.recibo = recibo;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Ingreso getIngreso() {
        return ingreso;
    }

    public void setIngreso(Ingreso ingreso) {
        this.ingreso = ingreso;
    }

}
