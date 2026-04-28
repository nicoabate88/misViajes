
package abate.abate.entidades;

public class ClienteEstadistica {
    
    private int year;
    private int month;
    private int flete;
    private double neto;
    private double km;
    private double rentabilidad;
    private double tarifa;
    private double kg;

    public ClienteEstadistica() {
    }

    public ClienteEstadistica(int year, int month, int flete, double neto, double km, double rentabilidad, double tarifa, double kg) {
        this.year = year;
        this.month = month;
        this.flete = flete;
        this.neto = neto;
        this.km = km;
        this.rentabilidad = rentabilidad;
        this.tarifa = tarifa;
        this.kg = kg;
    }

    public ClienteEstadistica(int year, int month, int flete) {
        this.year = year;
        this.month = month;
        this.flete = flete;
    }

    public ClienteEstadistica(int year, int month, int flete, double neto) {
        this.year = year;
        this.month = month;
        this.flete = flete;
        this.neto = neto;
    }

    public ClienteEstadistica(int year, int month, int flete, double neto, double km, double kg) {
        this.year = year;
        this.month = month;
        this.flete = flete;
        this.neto = neto;
        this.km = km;
        this.kg = kg;
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

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public double getRentabilidad() {
        return rentabilidad;
    }

    public void setRentabilidad(double rentabilidad) {
        this.rentabilidad = rentabilidad;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public double getKg() {
        return kg;
    }

    public void setKg(double kg) {
        this.kg = kg;
    }
    
    
    
}
