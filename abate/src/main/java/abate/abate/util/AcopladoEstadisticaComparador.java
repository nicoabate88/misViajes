
package abate.abate.util;

import abate.abate.entidades.AcopladoEstadistica;
import java.util.Comparator;

public class AcopladoEstadisticaComparador {
    
         public static Comparator<AcopladoEstadistica> ordenarMes = new Comparator<AcopladoEstadistica>() {
        
            @Override
            public int compare(AcopladoEstadistica o1, AcopladoEstadistica o2) {
                
                int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
                if (yearComparison != 0) {
                    return yearComparison;
                }
               
                return Integer.compare(o2.getMonth(), o1.getMonth());
            }
        };
    
}
