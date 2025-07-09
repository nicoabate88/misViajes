
package abate.abate.util;

import abate.abate.entidades.Caja;
import java.util.Comparator;

public class CajaComparador {
    
        public static Comparator<Caja> ordenarNombreChoferAsc = new Comparator<Caja>() {
        @Override
        public int compare(Caja c1, Caja c2) {
            return c1.getChofer().getNombre().compareTo(c2.getChofer().getNombre());
        }
    };
    
}
