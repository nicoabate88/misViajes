
package abate.abate.util;

import abate.abate.entidades.Azul;
import java.util.Comparator;


public class AzulComparador {
    
    public static Comparator<Azul> ordenarFechaDesc = new Comparator<Azul>() {
        @Override
        public int compare(Azul a1, Azul a2) {
            return a2.getFecha().compareTo(a1.getFecha());
        }
    };
    
    public static Comparator<Azul> ordenarIdDesc = new Comparator<Azul>() {
        @Override
        public int compare(Azul a1, Azul a2) {
            return a2.getId().compareTo(a1.getId());
        }
    };
    
}
