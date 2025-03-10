
package abate.abate.util;

import abate.abate.entidades.Producto;
import java.util.Comparator;

public class ProductoComparador {
    
        public static Comparator<Producto> ordenarNombreAsc = new Comparator<Producto>() {
        @Override
        public int compare(Producto c1, Producto c2) {
            return c1.getNombre().compareTo(c2.getNombre());
        }
    };
    
    
}
