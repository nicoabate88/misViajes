package abate.abate.util;

import abate.abate.entidades.ClienteEstadistica;
import java.util.Comparator;

public class ClienteEstadisticaComparador {

    public static Comparator<ClienteEstadistica> ordenarMes = new Comparator<ClienteEstadistica>() {

        @Override
        public int compare(ClienteEstadistica o1, ClienteEstadistica o2) {

            int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
            if (yearComparison != 0) {
                return yearComparison;
            }

            return Integer.compare(o2.getMonth(), o1.getMonth());
        }
    };

    public static Comparator<ClienteEstadistica> ordenarMesAsc = new Comparator<ClienteEstadistica>() {

        @Override
        public int compare(ClienteEstadistica o1, ClienteEstadistica o2) {

            int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
            if (yearComparison != 0) {
                return yearComparison;
            }

            return Integer.compare(o1.getMonth(), o2.getMonth());
        }
    };

}
