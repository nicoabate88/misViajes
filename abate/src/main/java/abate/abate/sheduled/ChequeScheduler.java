
package abate.abate.sheduled;

import abate.abate.servicios.ChequeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChequeScheduler {
    
    @Autowired
    private ChequeServicio chequeServicio;

    
    //TODOS LOS DIAS 00:05 
    @Scheduled(cron = "0 5 0 * * *")
    //@Scheduled(cron = "*/30 * * * * *")
    public void acreditarChequesAutomaticamente() {

        try {

            chequeServicio.acreditarChequesVencidos();

            //System.out.println("Proceso automático de acreditación ejecutado correctamente.");

        } catch (Exception e) {

            //System.out.println("Error en acreditación automática: " + e.getMessage());

        }

    }
    
}
