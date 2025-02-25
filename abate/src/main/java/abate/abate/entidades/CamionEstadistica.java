package abate.abate.entidades;

public class CamionEstadistica {

    private int year;
    private int month;
    private int flete;
    private double neto;
    private double kmRecorrido;
    private double litro;
    private double consumo;
    private double gasto;
    private double rentabilidad;

    public CamionEstadistica() {
    }

    public CamionEstadistica(int year, int month, int flete, double neto) {
        this.year = year;
        this.month = month;
        this.flete = flete;
        this.neto = neto;
    }

    public CamionEstadistica(int year, int month, double kmRecorrido, double litro) {
        this.year = year;
        this.month = month;
        this.kmRecorrido = kmRecorrido;
        this.litro = litro;
    }

    public CamionEstadistica(int year, int month, double gasto) {
        this.year = year;
        this.month = month;
        this.gasto = gasto;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    public double getGasto() {
        return gasto;
    }

    public void setGasto(double gasto) {
        this.gasto = gasto;
    }

    public double getRentabilidad() {
        return rentabilidad;
    }

    public void setRentabilidad(double rentabilidad) {
        this.rentabilidad = rentabilidad;
    }

}
