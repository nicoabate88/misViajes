
package abate.abate.util;

import abate.abate.entidades.Proveedor;
import java.util.Comparator;

public class ProveedorComparador {
    
    public static Comparator<Proveedor> ordenarNombreAsc = new Comparator<Proveedor>() {
        @Override
        public int compare(Proveedor c1, Proveedor c2) {
            return c1.getNombre().compareTo(c2.getNombre());
        }
    };
    
}
