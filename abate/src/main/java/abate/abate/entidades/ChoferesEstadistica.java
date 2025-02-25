package abate.abate.entidades;

public class ChoferesEstadistica {

    private int flete;
    private double neto;
    private double kmRecorrido;
    private double litro;
    private double consumo;
    private double azul;
    private double lubricante;
    private double gasto;
    private Camion camion;
    private double rentabilidad;

    public ChoferesEstadistica() {
    }

    public int getFlete() {
        return flete;
    }

    public void setFlete(int flete) {
        this.flete = flete;
    }

    public double getNeto() {
        return neto;
    }

    public void setNeto(double neto) {
        this.neto = neto;
    }

    public double getKmRecorrido() {
        return kmRecorrido;
    }

    public void setKmRecorrido(double kmRecorrido) {
        this.kmRecorrido = kmRecorrido;
    }

    public double getLitro() {
        return litro;
    }

    public void setLitro(double litro) {
        this.litro = litro;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public double getAzul() {
        return azul;
    }

    public void setAzul(double azul) {
        this.azul = azul;
    }

    public double getLubricante() {
        return lubricante;
    }

    public void setLubricante(double lubricante) {
        this.lubricante = lubricante;
    }

    public double getGasto() {
        return gasto;
    }

    public void setGasto(double gasto) {
        this.gasto = gasto;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public double getRentabilidad() {
        return rentabilidad;
    }

    public void setRentabilidad(double rentabilidad) {
        this.rentabilidad = rentabilidad;
    }

}
