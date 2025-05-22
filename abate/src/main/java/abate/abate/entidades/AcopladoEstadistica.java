
package abate.abate.entidades;

public class AcopladoEstadistica {
    
    private int year;
    private int month;
    private int flete;
    private double kmRecorrido;

    public AcopladoEstadistica() {
    }
    
    public AcopladoEstadistica(int year, int month, int flete) {
        this.year = year;
        this.month = month;
        this.flete = flete;
    }

    public AcopladoEstadistica(int year, int month, double kmRecorrido) {
        this.year = year;
        this.month = month;
        this.kmRecorrido = kmRecorrido;
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

    public double getKmRecorrido() {
        return kmRecorrido;
    }

    public void setKmRecorrido(double kmRecorrido) {
        this.kmRecorrido = kmRecorrido;
    }
    
}


