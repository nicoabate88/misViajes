
package abate.abate.repositorios;

import abate.abate.entidades.TipoMantenimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMantenimientoRepositorio extends JpaRepository<TipoMantenimiento, Long> {
    
    @Query("SELECT MAX(t) FROM TipoMantenimiento t WHERE t.idOrg = :id")
    public TipoMantenimiento ultimoTipo(@Param("id") Long id);  
    
    @Query("SELECT t FROM TipoMantenimiento t WHERE t.idOrg = :id") 
    public List<TipoMantenimiento> buscarTipos(@Param("id") Long id);
    
    @Query("SELECT t FROM TipoMantenimiento t WHERE t.idOrg = :id AND t.nombre = :nombre") 
    public TipoMantenimiento buscarTipoPorNombre(@Param("id") Long id, @Param("nombre") String nombre);
    
    @Query("SELECT t FROM TipoMantenimiento t WHERE t.idOrg = :id AND :aplicaA MEMBER OF t.aplicaA")
    List<TipoMantenimiento> findByAplicaA(@Param("id") Long id, @Param("aplicaA") TipoMantenimiento.AplicaA aplicaA);
    
    @Query("SELECT t FROM TipoMantenimiento t WHERE t.idOrg = :id AND t.clase = 'PREVENTIVO' AND :aplicaA MEMBER OF t.aplicaA")
    List<TipoMantenimiento> findByAplicaAPreventivo(@Param("id") Long id, @Param("aplicaA") TipoMantenimiento.AplicaA aplicaA);
    
    @Query("SELECT t FROM TipoMantenimiento t WHERE t.idOrg = :id AND :aplicaA MEMBER OF t.aplicaA")
    List<TipoMantenimiento> findOrdenByAplicaA(@Param("id") Long id, @Param("aplicaA") TipoMantenimiento.AplicaA aplicaA);
    
    @Query("SELECT COUNT(tm) > 0 FROM TipoMantenimiento tm JOIN tm.aplicaA a WHERE tm.idOrg = :id AND tm.id = :idTipo AND a = :aplicaA")
    boolean existsByIdAndAplicaA(@Param("id") Long id, @Param("idTipo") Long idTipo, @Param("aplicaA") TipoMantenimiento.AplicaA aplicaA);
    
}
