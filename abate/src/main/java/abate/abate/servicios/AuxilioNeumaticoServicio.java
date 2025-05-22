
package abate.abate.servicios;

import abate.abate.entidades.AuxilioNeumatico;
import abate.abate.repositorios.AuxilioNeumaticoRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuxilioNeumaticoServicio {
    
    @Autowired
    private AuxilioNeumaticoRepositorio auxilioRepositorio;
    
        public List<AuxilioNeumatico> buscarAuxiliosVigenteCamion(Long idCamion){
        
        return auxilioRepositorio.buscarAuxiliosIdCamionVigente(idCamion);
        
    }
    
    public List<AuxilioNeumatico> buscarAuxiliosVigenteAcoplado(Long idAcoplado){
        
        return auxilioRepositorio.buscarAuxiliosIdAcopladoVigente(idAcoplado);
        
    }
    
        public AuxilioNeumatico buscarAuxilioIdNeumatico(Long id){
        
        Optional<AuxilioNeumatico> auxilio = auxilioRepositorio.findByNeumaticoIdAndEstado(id, "VIGENTE");
        
        AuxilioNeumatico aux = new AuxilioNeumatico();
        if(auxilio.isPresent()){
            aux = auxilio.get();
        }
        
        return aux;
        
    }
    
}
