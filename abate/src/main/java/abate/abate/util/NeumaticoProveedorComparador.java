
package abate.abate.util;

import abate.abate.entidades.NeumaticoProveedor;
import java.util.Comparator;

public class NeumaticoProveedorComparador {
    
        public static Comparator<NeumaticoProveedor> ordenarNombreAsc = new Comparator<NeumaticoProveedor>() {
        @Override
        public int compare(NeumaticoProveedor p1, NeumaticoProveedor p2) {
            return p1.getNombre().compareTo(p2.getNombre());
        }
    };
    
}
