
package abate.abate.util;

import abate.abate.entidades.TipoDocumentacion;
import java.util.Comparator;

public class TipoDocumentacionComparador {
    
        public static Comparator<TipoDocumentacion> ordenarNombreAsc = new Comparator<TipoDocumentacion>() {
        @Override
        public int compare(TipoDocumentacion s1, TipoDocumentacion s2) {
            return s1.getNombre().compareTo(s2.getNombre());
        }
    };
    
}
