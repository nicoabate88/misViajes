
package abate.abate.util;

import abate.abate.entidades.Neumatico;
import java.util.Comparator;

public class NeumaticoComparador {
    
        public static Comparator<Neumatico> ordenarNumAsc = new Comparator<Neumatico>() {
        @Override
        public int compare(Neumatico n1, Neumatico n2) {
            return n1.getNumero().compareTo(n2.getNumero());
        }
    };
    
}
