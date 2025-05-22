
package abate.abate.util;

import abate.abate.entidades.Acoplado;
import java.util.Comparator;

public class AcopladoComparador {
    
        public static Comparator<Acoplado> ordenarDominioAsc = new Comparator<Acoplado>() {
        @Override
        public int compare(Acoplado c1, Acoplado c2) {
            return c1.getDominio().compareTo(c2.getDominio());
        }
    };
    
}
