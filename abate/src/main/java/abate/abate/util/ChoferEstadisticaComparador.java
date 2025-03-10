
package abate.abate.util;

import abate.abate.entidades.ChoferEstadistica;
import java.util.Comparator;

public class ChoferEstadisticaComparador {
    
         public static Comparator<ChoferEstadistica> ordenarMes = new Comparator<ChoferEstadistica>() {
        
            @Override
            public int compare(ChoferEstadistica o1, ChoferEstadistica o2) {
                
                int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
                if (yearComparison != 0) {
                    return yearComparison;
                }
               
                return Integer.compare(o2.getMonth(), o1.getMonth());
            }
        };
         
                  public static Comparator<ChoferEstadistica> ordenarMesAsc = new Comparator<ChoferEstadistica>() {
        
            @Override
            public int compare(ChoferEstadistica o1, ChoferEstadistica o2) {
                
                int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
                if (yearComparison != 0) {
                    return yearComparison;
                }
               
                return Integer.compare(o1.getMonth(), o2.getMonth());
            }
        };
    
}
