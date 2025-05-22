
package abate.abate.repositorios;

import abate.abate.entidades.HistorialNeumatico;
import abate.abate.entidades.Neumatico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialNeumaticoRepositorio extends JpaRepository<HistorialNeumatico, Long> {
    
    Optional<HistorialNeumatico> findByNeumaticoAndEstado(Neumatico neumatico, String estado);
    
    boolean existsByNeumaticoId(Long id);
    
    @Query("SELECT h FROM HistorialNeumatico h WHERE h.neumatico.id = :neumaticoId AND h.id < :historialId ORDER BY h.id DESC")
    Optional<HistorialNeumatico> findAnteriorByNeumatico(@Param("neumaticoId") Long neumaticoId, @Param("historialId") Long historialId);
    
}
