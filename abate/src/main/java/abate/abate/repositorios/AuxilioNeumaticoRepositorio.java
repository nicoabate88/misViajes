
package abate.abate.repositorios;

import abate.abate.entidades.Acoplado;
import abate.abate.entidades.AuxilioNeumatico;
import abate.abate.entidades.Camion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuxilioNeumaticoRepositorio extends JpaRepository<AuxilioNeumatico, Long> {
    
    @Query("SELECT a FROM AuxilioNeumatico a WHERE camion_id = :id AND a.estado = 'VIGENTE'")
    public List<AuxilioNeumatico> buscarAuxiliosIdCamionVigente(@Param("id") Long id);
    
    @Query("SELECT a FROM AuxilioNeumatico a WHERE acoplado_id = :id AND a.estado = 'VIGENTE'")
    public List<AuxilioNeumatico> buscarAuxiliosIdAcopladoVigente(@Param("id") Long id);
    
    Optional<AuxilioNeumatico> findByNeumaticoIdAndEstado(Long id, String estado);
    
    List<AuxilioNeumatico> findByCamionAndEstado(Camion camion, String estado);
    
    List<AuxilioNeumatico> findByAcopladoAndEstado(Acoplado acoplado, String estado);
    
    boolean existsByNeumaticoId(Long idNeumatico);
    
}
