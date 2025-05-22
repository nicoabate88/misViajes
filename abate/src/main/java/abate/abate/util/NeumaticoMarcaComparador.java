
package abate.abate.util;

import abate.abate.entidades.NeumaticoMarca;
import java.util.Comparator;


public class NeumaticoMarcaComparador {
    
    public static Comparator<NeumaticoMarca> ordenarNombreAsc = new Comparator<NeumaticoMarca>() {
        @Override
        public int compare(NeumaticoMarca p1, NeumaticoMarca p2) {
            return p1.getMarca().compareTo(p2.getMarca());
        }
    };
    
}
