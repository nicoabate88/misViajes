
package abate.abate.util;

import abate.abate.entidades.TipoMantenimiento;
import java.util.Comparator;

public class TipoMantenimientoComparador {
    
        public static Comparator<TipoMantenimiento> ordenarNombreAsc = new Comparator<TipoMantenimiento>() {
        @Override
        public int compare(TipoMantenimiento s1, TipoMantenimiento s2) {
            return s1.getNombre().compareTo(s2.getNombre());
        }
    };
    
}
