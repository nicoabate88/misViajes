
package abate.abate.util;

import abate.abate.entidades.Combustible;
import java.util.Comparator;

public class CombustibleComparador {
    
    public static Comparator<Combustible> ordenarFechaDesc = new Comparator<Combustible>() {
        @Override
        public int compare(Combustible c1, Combustible c2) {
            return c2.getFechaCarga().compareTo(c1.getFechaCarga());
        }
    };
    
    public static Comparator<Combustible> ordenarIdDesc = new Comparator<Combustible>() {
        @Override
        public int compare(Combustible c1, Combustible c2) {
            return c2.getId().compareTo(c1.getId());
        }
    };
    
        public static Comparator<Combustible> ordenarFechaAsc = new Comparator<Combustible>() {
        @Override
        public int compare(Combustible c1, Combustible c2) {
            return c1.getFechaCarga().compareTo(c2.getFechaCarga());
        }
    };
    
}
