
package abate.abate.repositorios;

import abate.abate.entidades.HistorialRecapado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialRecapadoRepositorio extends JpaRepository<HistorialRecapado, Long> {
    
    @Query("SELECT r FROM HistorialRecapado r WHERE r.estado = 'EN RECAPADO' AND r.idOrg = :id")
    List<HistorialRecapado> findByEnRecapado(@Param("id") Long id);
    
    @Query("SELECT r FROM HistorialRecapado r WHERE r.estado = 'VIGENTE' AND r.idOrg = :id")
    List<HistorialRecapado> findByVigente(@Param("id") Long id);
    
    @Query("SELECT MAX(r) FROM HistorialRecapado r WHERE r.idOrg = :id")
    public HistorialRecapado ultimoRecapado(@Param("id") Long id);
    
}
