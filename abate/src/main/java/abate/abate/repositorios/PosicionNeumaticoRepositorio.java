
package abate.abate.repositorios;

import abate.abate.entidades.Eje;
import abate.abate.entidades.PosicionNeumatico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosicionNeumaticoRepositorio extends JpaRepository<PosicionNeumatico, Long> {
    
     Optional<PosicionNeumatico> findByEjeAndPosicionAndEstado(Eje eje, Integer posicion, String estado);
     
     boolean existsByEje(Eje eje);
     
     boolean existsByNeumaticoId(Long idNeumatico);
    
}
